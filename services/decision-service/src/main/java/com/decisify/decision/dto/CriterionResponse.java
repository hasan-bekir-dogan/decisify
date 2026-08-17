package com.decisify.decision.dto;

import java.util.UUID;

public record CriterionResponse(
    UUID id,
    UUID decisionId,
    String name,
    Float weight,
    String unit,
    Boolean higherIsBetter
) {
    
}
