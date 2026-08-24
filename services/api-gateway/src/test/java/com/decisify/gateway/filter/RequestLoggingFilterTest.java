package com.decisify.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestLoggingFilterTest {

    @Test
    void shouldGenerateAndForwardRequestId() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        AtomicReference<String> forwardedRequestId = new AtomicReference<>();

        when(chain.filter(any())).thenAnswer(invocation -> {
            ServerWebExchange forwardedExchange = invocation.getArgument(0);

            forwardedRequestId.set(
                    forwardedExchange.getRequest()
                            .getHeaders()
                            .getFirst("X-Request-Id")
            );

            return Mono.empty();
        });

        RequestLoggingFilter filter = new RequestLoggingFilter();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/decisions")
                .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertNotNull(forwardedRequestId.get());
        assertFalse(forwardedRequestId.get().isBlank());

        verify(chain).filter(any());
    }

    @Test
    void shouldReplaceClientProvidedRequestId() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        AtomicReference<String> forwardedRequestId = new AtomicReference<>();

        when(chain.filter(any())).thenAnswer(invocation -> {
            ServerWebExchange forwardedExchange = invocation.getArgument(0);

            forwardedRequestId.set(
                    forwardedExchange.getRequest()
                            .getHeaders()
                            .getFirst("X-Request-Id")
            );

            return Mono.empty();
        });

        RequestLoggingFilter filter = new RequestLoggingFilter();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/decisions")
                .header("X-Request-Id", "fake-client-id")
                .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertNotNull(forwardedRequestId.get());
        assertNotEquals("fake-client-id", forwardedRequestId.get());
    }
}