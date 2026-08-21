package com.decisify.decision.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RecommendationResponse(
        UUID id,
        UUID decisionId,
        UUID selectedAlternativeId,
        Float finalScore,
        String explanation,
        OffsetDateTime createdAt
) {
}