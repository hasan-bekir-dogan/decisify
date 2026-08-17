package com.decisify.decision.exception;

public class InvalidCriterionWeightException extends RuntimeException {

    public InvalidCriterionWeightException(
            Float currentTotalWeight,
            Float requestedWeight
    ) {
        super(
                "Criterion weights cannot exceed 100%. Current total: "
                        + currentTotalWeight
                        + ", requested weight: "
                        + requestedWeight
        );
    }
}