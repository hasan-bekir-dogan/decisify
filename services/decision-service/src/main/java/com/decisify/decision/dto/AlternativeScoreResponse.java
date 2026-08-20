package com.decisify.decision.dto;

import java.util.List;
import java.util.UUID;

public record AlternativeScoreResponse(
        UUID alternativeId,
        Float score,
        List<CriterionContribution> contributions
) {
}