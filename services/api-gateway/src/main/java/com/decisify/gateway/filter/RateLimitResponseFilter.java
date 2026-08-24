package com.decisify.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class RateLimitResponseFilter implements GlobalFilter, Ordered {

    private static final String RATE_LIMIT_BODY = """
            {
              "status": 429,
              "error": "Too Many Requests",
              "message": "Rate limit exceeded. Please try again later."
            }
            """;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse originalResponse = exchange.getResponse();

        ServerHttpResponseDecorator decoratedResponse =
                new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> setComplete() {

                        if (getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {

                            byte[] bytes =
                                    RATE_LIMIT_BODY.getBytes(StandardCharsets.UTF_8);

                            getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            getHeaders().setContentLength(bytes.length);

                            DataBuffer buffer =
                                    bufferFactory().wrap(bytes);

                            return writeWith(Mono.just(buffer));
                        }

                        return super.setComplete();
                    }
                };

        ServerWebExchange mutatedExchange = exchange.mutate()
                .response(decoratedResponse)
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}