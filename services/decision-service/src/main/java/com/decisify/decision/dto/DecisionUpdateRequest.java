package com.decisify.decision.dto;

import jakarta.validation.constraints.NotBlank;

public record DecisionUpdateRequest(

    @NotBlank
    String title,

    String description
) {
    
}
