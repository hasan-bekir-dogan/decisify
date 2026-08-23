package com.decisify.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String fullName,
        OffsetDateTime createdAt
) {
}