package com.decisify.decision.exception;

import java.util.UUID;

public class AlternativeNotFoundException extends RuntimeException {
    
    public AlternativeNotFoundException(UUID id) {
        super("Alternative not found with id: " + id);
    }

}
