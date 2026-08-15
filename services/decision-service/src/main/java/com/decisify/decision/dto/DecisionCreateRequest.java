package com.decisify.decision.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DecisionCreateRequest (
    @NotNull
    UUID userId,

    @NotBlank
    String title,

    String description
) {
    
}
