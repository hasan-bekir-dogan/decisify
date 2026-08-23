package com.decisify.auth.security;

import com.decisify.auth.domain.User;
import com.decisify.auth.exception.InvalidRefreshTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(User user) {
        return generateToken(
                user,
                "access",
                jwtProperties.accessTokenExpiration()
        );
    }

    public String generateRefreshToken(User user) {
        return generateToken(
                user,
                "refresh",
                jwtProperties.refreshTokenExpiration()
        );
    }

    private String generateToken(
            User user,
            String tokenType,
            long expirationMillis
    ) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("type", tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(signingKey)
                .compact();
    }

    public UUID validateRefreshTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);

            if (!"refresh".equals(tokenType)) {
                throw new InvalidRefreshTokenException();
            }

            return UUID.fromString(claims.getSubject());

        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidRefreshTokenException();
        }
    }
}