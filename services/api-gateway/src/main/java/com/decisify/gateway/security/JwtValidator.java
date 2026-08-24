package com.decisify.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtValidator {

    private final SecretKey signingKey;

    public JwtValidator(JwtProperties jwtProperties) {
        this.signingKey = Keys.hmacShaKeyFor(
            jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public UUID validateAccessTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);

            if (!"access".equals(tokenType)) {
                throw new IllegalArgumentException("Invalid token type");
            }

            return UUID.fromString(claims.getSubject());

        } catch (JwtException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid access token", exception);
        }
    }
}