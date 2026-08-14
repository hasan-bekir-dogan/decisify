package com.decisify.decision.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "criterion_value",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_criterion_values_alternative_criterion",
            columnNames = {
                "alternative_id",
                "criterion_id"
            }
        )
    }
)
public class CriterionValue {
    
    @Id
    private UUID id;

    @Column(name = "alternative_id", nullable = false)
    private UUID alternativeId;

    @Column(name = "criterion_id", nullable = false)
    private UUID criterionId;

    @Column(name = "raw_value")
    private Float rawValue;

    @Column(name = "normalized_value")
    private Float normalizedValue;

    @Column(length = 100)
    private String source;

    private Float confidence;

    public CriterionValue() {

    }

    public CriterionValue(
        UUID id,
        UUID alternativeId,
        UUID criterionId,
        Float rawValue,
        Float normalizedValue,
        String source,
        Float confidence
    ) {
        this.id = id;
        this.alternativeId = alternativeId;
        this.criterionId = criterionId;
        this.rawValue = rawValue;
        this.normalizedValue = normalizedValue;
        this.source = source;
        this.confidence = confidence;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAlternativeId() {
        return alternativeId;
    }

    public UUID getCriterionId() {
        return criterionId;
    }

    public Float getRawValue() {
        return rawValue;
    }

    public String getSource() {
        return source;
    }

    public Float getConfidence() {
        return confidence;
    }
}
