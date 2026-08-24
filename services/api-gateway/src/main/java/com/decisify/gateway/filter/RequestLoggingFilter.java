package com.decisify.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request -> request.headers(headers -> {
                    headers.remove(REQUEST_ID_HEADER);
                    headers.add(REQUEST_ID_HEADER, requestId);
                }))
                .build();

        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long durationMs =
                            System.currentTimeMillis() - startTime;

                    int status = mutatedExchange.getResponse()
                            .getStatusCode() != null
                            ? mutatedExchange.getResponse()
                                    .getStatusCode()
                                    .value()
                            : 500;

                    log.info(
                            "{{\"timestamp\":\"{}\",\"requestId\":\"{}\",\"method\":\"{}\",\"path\":\"{}\",\"status\":{},\"durationMs\":{}}}",
                            Instant.now(),
                            requestId,
                            mutatedExchange.getRequest().getMethod(),
                            mutatedExchange.getRequest().getURI().getPath(),
                            status,
                            durationMs
                    );
                });
    }

    @Override
    public int getOrder() {
        return -300;
    }
}