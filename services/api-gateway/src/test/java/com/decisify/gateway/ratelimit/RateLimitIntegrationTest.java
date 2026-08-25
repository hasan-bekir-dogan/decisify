package com.decisify.gateway.ratelimit;

import com.decisify.gateway.security.JwtValidator;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(RateLimitIntegrationTest.TestConfig.class)
class RateLimitIntegrationTest {

    private static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:8.8.1"))
                    .withExposedPorts(6379);

    private static final WireMockServer decisionService =
            new WireMockServer(0);

    @LocalServerPort
    private int gatewayPort;

    @Autowired
    private JwtValidator jwtValidator;

    private WebTestClient client;

    @BeforeAll
    static void startInfrastructure() {
        redis.start();
        decisionService.start();
    }

    @AfterAll
    static void stopInfrastructure() {
        decisionService.stop();
        redis.stop();
    }

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add(
                "spring.data.redis.port",
                () -> redis.getMappedPort(6379)
        );

        registry.add(
                "DECISION_SERVICE_URL",
                () -> "http://localhost:" + decisionService.port()
        );
    }

    @BeforeEach
    void setUp() {
        client = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + gatewayPort)
                .build();

        reset(jwtValidator);
        decisionService.resetAll();

        when(jwtValidator.validateAccessTokenAndGetUserId("valid-token"))
                .thenReturn(
                        UUID.fromString(
                                "f3801751-dafc-48ab-b7b2-160e49a0b793"
                        )
                );

        decisionService.stubFor(
                get(urlEqualTo("/decisions"))
                        .willReturn(
                                okJson("""
                                        {
                                          "service": "decision"
                                        }
                                        """)
                        )
        );
    }

    @Test
    void shouldReturn429WhenRateLimitIsExceeded() {

        boolean rateLimited = false;

        for (int i = 0; i < 30; i++) {

            int status = client.get()
                    .uri("/api/decisions")
                    .header(
                            "Authorization",
                            "Bearer valid-token"
                    )
                    .exchange()
                    .returnResult(String.class)
                    .getStatus()
                    .value();

            if (status == 429) {
                rateLimited = true;
                break;
            }
        }

        if (!rateLimited) {
            throw new AssertionError(
                    "Expected gateway to return HTTP 429 after exceeding rate limit"
            );
        }
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