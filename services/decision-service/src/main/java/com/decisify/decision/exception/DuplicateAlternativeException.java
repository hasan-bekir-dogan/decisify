package com.decisify.decision.exception;

import java.util.UUID;

public class DuplicateAlternativeException extends RuntimeException {

    public DuplicateAlternativeException(UUID decisionId, String name) {
        super(
            "Alternative with name '" + name +
            "' already exists for decision: " + decisionId
        );
    }
}