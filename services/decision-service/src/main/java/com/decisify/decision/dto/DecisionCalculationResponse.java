package com.decisify.decision.dto;

import java.util.List;
import java.util.UUID;

public record DecisionCalculationResponse(
        UUID decisionId,
        List<RankedAlternativeResponse> alternatives
) {
}