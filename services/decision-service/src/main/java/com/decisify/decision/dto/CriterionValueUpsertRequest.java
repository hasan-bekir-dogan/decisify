package com.decisify.decision.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record CriterionValueUpsertRequest(
    @NotNull
    UUID alternativeId,

    @NotNull
    UUID criterionId,

    @NotNull
    Float rawValue
) {
    
}
