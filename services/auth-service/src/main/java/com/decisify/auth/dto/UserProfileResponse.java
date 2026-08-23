package com.decisify.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String fullName,
        OffsetDateTime createdAt
) {
}