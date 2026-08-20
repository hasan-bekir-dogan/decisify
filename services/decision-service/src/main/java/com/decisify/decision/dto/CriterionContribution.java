package com.decisify.decision.dto;

import java.util.UUID;

public record CriterionContribution(
        UUID criterionId,
        String criterionName,
        Float normalizedValue,
        Float weight,
        Float contribution
) {
}