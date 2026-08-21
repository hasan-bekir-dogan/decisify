package com.decisify.decision.dto;

import java.util.List;
import java.util.UUID;

public record RankedAlternativeResponse(
        UUID alternativeId,
        String alternativeName,
        Float score,
        Integer rank,
        List<CriterionContribution> contributions
) {
}