package com.decisify.decision.dto;

import java.util.UUID;

public record AlternativeResponse(
    UUID id,
    UUID decisionId,
    String name,
    String description
) {
    
}
