package com.decisify.decision.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DecisionApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldCreateDecision() throws Exception {

        UUID userId = UUID.randomUUID();

        String requestBody = """
            {
            "userId": "%s",
            "title": "Choose a laptop",
            "description": "Select the best laptop for development"
            }
            """.formatted(userId);

        mockMvc.perform(post("/decisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.userId").value(userId.toString()))
            .andExpect(jsonPath("$.title").value("Choose a laptop"))
            .andExpect(jsonPath("$.description")
                .value("Select the best laptop for development"))
            .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldCreateAndRetrieveDecision() throws Exception {

        UUID userId = UUID.randomUUID();

        String requestBody = """
            {
            "userId": "%s",
            "title": "Choose a laptop",
            "description": "Select the best laptop for development"
            }
            """.formatted(userId);

        String createResponse = mockMvc.perform(post("/decisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String decisionId = JsonPath
            .read(createResponse, "$.id")
            .toString();

        mockMvc.perform(get("/decisions/{id}", decisionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(decisionId))
            .andExpect(jsonPath("$.userId").value(userId.toString()))
            .andExpect(jsonPath("$.title").value("Choose a laptop"))
            .andExpect(jsonPath("$.description")
                .value("Select the best laptop for development"));
    }

    @Test
    void shouldCreateDecisionWithAlternativesAndCriteria() throws Exception {
        UUID userId = UUID.randomUUID();

        // 1. Create decision
        String decisionResponse = mockMvc.perform(post("/decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "userId": "%s",
                            "title": "Choose a laptop",
                            "description": "Select the best laptop for development"
                            }
                            """.formatted(userId)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String decisionId = JsonPath
                .read(decisionResponse, "$.id")
                .toString();

        // 2. Create MacBook alternative
        String macBookResponse = mockMvc.perform(
                        post("/decisions/{decisionId}/alternatives", decisionId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "name": "MacBook",
                                    "description": "Apple laptop"
                                    }
                                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("MacBook"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String macBookId = JsonPath
                .read(macBookResponse, "$.id")
                .toString();

        // 3. Create ThinkPad alternative
        String thinkPadResponse = mockMvc.perform(
                        post("/decisions/{decisionId}/alternatives", decisionId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "name": "ThinkPad",
                                    "description": "Lenovo laptop"
                                    }
                                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ThinkPad"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String thinkPadId = JsonPath
                .read(thinkPadResponse, "$.id")
                .toString();

        // 4. Create Performance criterion
        String performanceResponse = mockMvc.perform(
                        post("/decisions/{decisionId}/criteria", decisionId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "name": "Performance",
                                    "weight": 60,
                                    "unit": "score",
                                    "higherIsBetter": true
                                    }
                                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Performance"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String performanceCriterionId = JsonPath
                .read(performanceResponse, "$.id")
                .toString();

        // 5. Create Price criterion
        String priceResponse = mockMvc.perform(
                        post("/decisions/{decisionId}/criteria", decisionId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "name": "Price",
                                    "weight": 40,
                                    "unit": "EUR",
                                    "higherIsBetter": false
                                    }
                                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Price"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String priceCriterionId = JsonPath
                .read(priceResponse, "$.id")
                .toString();

        // Verify IDs were actually returned.
        org.junit.jupiter.api.Assertions.assertNotNull(macBookId);
        org.junit.jupiter.api.Assertions.assertNotNull(thinkPadId);
        org.junit.jupiter.api.Assertions.assertNotNull(performanceCriterionId);
        org.junit.jupiter.api.Assertions.assertNotNull(priceCriterionId);

        // 6. Set MacBook Performance = 90
        mockMvc.perform(post("/decisions/{decisionId}/values", decisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "alternativeId": "%s",
                            "criterionId": "%s",
                            "rawValue": 90
                            }
                            """.formatted(macBookId, performanceCriterionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawValue").value(90));

        // 7. Set ThinkPad Performance = 80
        mockMvc.perform(post("/decisions/{decisionId}/values", decisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "alternativeId": "%s",
                            "criterionId": "%s",
                            "rawValue": 80
                            }
                            """.formatted(thinkPadId, performanceCriterionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawValue").value(80));

        // 8. Set MacBook Price = 2000
        mockMvc.perform(post("/decisions/{decisionId}/values", decisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "alternativeId": "%s",
                            "criterionId": "%s",
                            "rawValue": 2000
                            }
                            """.formatted(macBookId, priceCriterionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawValue").value(2000));

        // 9. Set ThinkPad Price = 1500
        mockMvc.perform(post("/decisions/{decisionId}/values", decisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                            "alternativeId": "%s",
                            "criterionId": "%s",
                            "rawValue": 1500
                            }
                            """.formatted(thinkPadId, priceCriterionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawValue").value(1500));
        
        // 10. Calculate decision and verify ranking
        mockMvc.perform(post("/decisions/{decisionId}/calculate", decisionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decisionId").value(decisionId))

                // MacBook should win
                .andExpect(jsonPath("$.alternatives[0].alternativeId")
                        .value(macBookId))
                .andExpect(jsonPath("$.alternatives[0].alternativeName")
                        .value("MacBook"))
                .andExpect(jsonPath("$.alternatives[0].score")
                        .value(60.0))
                .andExpect(jsonPath("$.alternatives[0].rank")
                        .value(1))

                // ThinkPad should be second
                .andExpect(jsonPath("$.alternatives[1].alternativeId")
                        .value(thinkPadId))
                .andExpect(jsonPath("$.alternatives[1].alternativeName")
                        .value("ThinkPad"))
                .andExpect(jsonPath("$.alternatives[1].score")
                        .value(40.0))
                .andExpect(jsonPath("$.alternatives[1].rank")
                        .value(2));
    }
}