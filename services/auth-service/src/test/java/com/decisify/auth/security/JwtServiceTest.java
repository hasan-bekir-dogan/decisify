package com.decisify.auth.security;

import com.decisify.auth.domain.User;
import com.decisify.auth.exception.InvalidAccessTokenException;
import com.decisify.auth.exception.InvalidRefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "12345678901234567890123456789012",
                900000,
                604800000
        );

        jwtService = new JwtService(properties);

        user = new User(
                UUID.randomUUID(),
                "test@example.com",
                "hashed-password",
                "Test User",
                OffsetDateTime.now()
        );
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String token = jwtService.generateAccessToken(user);

        UUID userId = jwtService.validateAccessTokenAndGetUserId(token);

        assertEquals(user.getId(), userId);
    }

    @Test
    void shouldGenerateAndValidateRefreshToken() {
        String token = jwtService.generateRefreshToken(user);

        UUID userId = jwtService.validateRefreshTokenAndGetUserId(token);

        assertEquals(user.getId(), userId);
    }

    @Test
    void shouldRejectAccessTokenAsRefreshToken() {
        String accessToken = jwtService.generateAccessToken(user);

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> jwtService.validateRefreshTokenAndGetUserId(accessToken)
        );
    }

    @Test
    void shouldRejectExpiredAccessToken() throws InterruptedException {
        JwtProperties shortLivedProperties = new JwtProperties(
                "12345678901234567890123456789012",
                1,
                604800000);

        JwtService shortLivedJwtService = new JwtService(shortLivedProperties);

        String token = shortLivedJwtService.generateAccessToken(user);

        Thread.sleep(10);

        assertThrows(
                InvalidAccessTokenException.class,
                () -> shortLivedJwtService.validateAccessTokenAndGetUserId(token));
    }
}