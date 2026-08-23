package com.decisify.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType
) {
}