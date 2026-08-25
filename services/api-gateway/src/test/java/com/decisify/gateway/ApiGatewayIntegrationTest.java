package com.decisify.gateway;

import com.decisify.gateway.security.JwtValidator;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(ApiGatewayIntegrationTest.TestConfig.class)
class ApiGatewayIntegrationTest {

    private static final WireMockServer authService =
            new WireMockServer(0);

    private static final WireMockServer decisionService =
            new WireMockServer(0);

    private static final WireMockServer documentService =
            new WireMockServer(0);

    private static final WireMockServer genaiService =
            new WireMockServer(0);

    private static final WireMockServer simulationService =
            new WireMockServer(0);

    @LocalServerPort
    private int gatewayPort;

    @Autowired
    private JwtValidator jwtValidator;

    private WebTestClient client;

    @BeforeAll
    static void startMockServices() {
        authService.start();
        decisionService.start();
        documentService.start();
        genaiService.start();
        simulationService.start();
    }

    @AfterAll
    static void stopMockServices() {
        authService.stop();
        decisionService.stop();
        documentService.stop();
        genaiService.stop();
        simulationService.stop();
    }

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "AUTH_SERVICE_URL",
                () -> "http://localhost:" + authService.port()
        );

        registry.add(
                "DECISION_SERVICE_URL",
                () -> "http://localhost:" + decisionService.port()
        );

        registry.add(
                "DOCUMENT_SERVICE_URL",
                () -> "http://localhost:" + documentService.port()
        );

        registry.add(
                "GENAI_SERVICE_URL",
                () -> "http://localhost:" + genaiService.port()
        );

        registry.add(
                "SIMULATION_SERVICE_URL",
                () -> "http://localhost:" + simulationService.port()
        );
    }

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + gatewayPort)
                .build();

        reset(jwtValidator);

        when(jwtValidator.validateAccessTokenAndGetUserId("valid-token"))
                .thenReturn(UUID.fromString(
                        "f3801751-dafc-48ab-b7b2-160e49a0b793"
                ));

        when(jwtValidator.validateAccessTokenAndGetUserId("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        authService.resetAll();
        decisionService.resetAll();
        documentService.resetAll();
        genaiService.resetAll();
        simulationService.resetAll();
    }

    @Test
    void shouldRouteAuthRequestsToAuthService() {
        authService.stubFor(post(urlEqualTo("/auth/login"))
                .willReturn(okJson("""
                        {"service":"auth"}
                        """)));

        client.post()
                .uri("/api/auth/login")
                .bodyValue("""
                        {
                          "email": "test@example.com",
                          "password": "Password123!"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("auth");

        authService.verify(
                1,
                postRequestedFor(urlEqualTo("/auth/login"))
        );
    }

    @Test
    void shouldRouteDecisionRequestsToDecisionService() {
        decisionService.stubFor(get(urlEqualTo("/decisions"))
                .willReturn(okJson("""
                        {"service":"decision"}
                        """)));

        client.get()
                .uri("/api/decisions")
                .header("Authorization", "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("decision");

        decisionService.verify(
                1,
                getRequestedFor(urlEqualTo("/decisions"))
        );
    }

    @Test
    void shouldRouteDocumentRequestsToDocumentService() {
        documentService.stubFor(get(urlEqualTo("/documents"))
                .willReturn(okJson("""
                        {"service":"document"}
                        """)));

        client.get()
                .uri("/api/documents")
                .header("Authorization", "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("document");

        documentService.verify(
                1,
                getRequestedFor(urlEqualTo("/documents"))
        );
    }

    @Test
    void shouldRouteGenAiRequestsToGenAiService() {
        genaiService.stubFor(get(urlEqualTo("/ai"))
                .willReturn(okJson("""
                        {"service":"genai"}
                        """)));

        client.get()
                .uri("/api/ai")
                .header("Authorization", "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("genai");

        genaiService.verify(
                1,
                getRequestedFor(urlEqualTo("/ai"))
        );
    }

    @Test
    void shouldRouteSimulationRequestsToSimulationService() {
        simulationService.stubFor(get(urlEqualTo("/simulations"))
                .willReturn(okJson("""
                        {"service":"simulation"}
                        """)));

        client.get()
                .uri("/api/simulations")
                .header("Authorization", "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("simulation");

        simulationService.verify(
                1,
                getRequestedFor(urlEqualTo("/simulations"))
        );
    }

    @Test
    void shouldRejectProtectedRequestWithoutJwt() {
        client.get()
                .uri("/api/documents")
                .exchange()
                .expectStatus().isUnauthorized();

        documentService.verify(
                0,
                getRequestedFor(anyUrl())
        );
    }

    @Test
    void shouldRejectProtectedRequestWithInvalidJwt() {
        client.get()
                .uri("/api/documents")
                .header("Authorization", "Bearer invalid-token")
                .exchange()
                .expectStatus().isUnauthorized();

        documentService.verify(
                0,
                getRequestedFor(anyUrl())
        );
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestConfig {

        @Bean
        @Primary
        JwtValidator testJwtValidator() {
            return mock(JwtValidator.class);
        }
    }
}