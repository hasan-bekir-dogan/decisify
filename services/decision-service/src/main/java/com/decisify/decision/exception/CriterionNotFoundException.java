package com.decisify.decision.exception;

import java.util.UUID;

public class CriterionNotFoundException extends RuntimeException{

    public CriterionNotFoundException(UUID criterionId) {
        super("Criterion not found with id: " + criterionId);
    }
}
