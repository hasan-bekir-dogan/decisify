package com.decisify.decision.exception;

import java.util.UUID;

public class RecommendationNotFoundException extends RuntimeException {

    public RecommendationNotFoundException(UUID decisionId) {
        super("No recommendation found for decision: " + decisionId);
    }
}