package com.decisify.decision.dto;

import java.util.UUID;

public record CriterionValueResponse(
        UUID id,
        UUID alternativeId,
        UUID criterionId,
        Float rawValue,
        Float normalizedValue,
        String source,
        Float confidence
) {
}