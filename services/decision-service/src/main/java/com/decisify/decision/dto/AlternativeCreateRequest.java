package com.decisify.decision.dto;

import jakarta.validation.constraints.NotBlank;

public record AlternativeCreateRequest (

    @NotBlank
    String name,
    
    String description
) {
    
}
