package com.decisify.decision.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.decisify.decision.domain.DecisionStatus;

public record DecisionResponse(
    UUID id,
    UUID userId,
    String title,
    String description,
    DecisionStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    
}
