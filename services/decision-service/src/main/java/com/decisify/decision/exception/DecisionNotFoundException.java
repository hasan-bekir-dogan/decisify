package com.decisify.decision.exception;

import java.util.UUID;

public class DecisionNotFoundException extends RuntimeException{
    
    public DecisionNotFoundException(UUID id) {
        super("Decision not found with id: " + id);
    }
}
