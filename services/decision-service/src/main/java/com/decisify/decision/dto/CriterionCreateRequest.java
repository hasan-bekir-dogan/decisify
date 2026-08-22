package com.decisify.decision.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriterionCreateRequest(
    @NotBlank
    String name,

    @NotNull
    @Min(0)
    @Max(100)
    Float weight,

    @NotBlank
    String unit,

    @NotNull
    Boolean higherIsBetter
) {
    
}
