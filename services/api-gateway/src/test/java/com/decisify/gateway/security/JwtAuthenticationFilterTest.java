package com.decisify.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Test
    void shouldForwardValidatedUserIdToDownstreamService() {
        JwtValidator jwtValidator = mock(JwtValidator.class);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        UUID userId = UUID.randomUUID();

        when(jwtValidator.validateAccessTokenAndGetUserId("valid-token"))
                .thenReturn(userId);

        AtomicReference<String> forwardedUserId = new AtomicReference<>();

        when(chain.filter(any())).thenAnswer(invocation -> {
                ServerWebExchange forwardedExchange = invocation.getArgument(0);

                forwardedUserId.set(
                        forwardedExchange.getRequest()
                                .getHeaders()
                                .getFirst("X-User-Id")
                );

                return Mono.empty();
        });

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtValidator);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/decisions")
                .header("Authorization", "Bearer valid-token")
                .header("X-User-Id", "fake-user-id")
                .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertEquals(userId.toString(), forwardedUserId.get());
    }
}