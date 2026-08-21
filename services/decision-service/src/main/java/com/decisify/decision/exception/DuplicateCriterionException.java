package com.decisify.decision.exception;

import java.util.UUID;

public class DuplicateCriterionException extends RuntimeException {

    public DuplicateCriterionException(UUID decisionId, String name) {
        super(
            "Criterion with name '" + name +
            "' already exists for decision: " + decisionId
        );
    }
}