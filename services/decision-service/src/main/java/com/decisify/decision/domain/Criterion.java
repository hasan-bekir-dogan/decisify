package com.decisify.decision.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "criteria")
public class Criterion {
    
    @Id
    private UUID id;

    @Column(name = "decision_id", nullable = false)
    private UUID decisionId;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Float weight;

    @Column(length = 100, nullable = false)
    private String unit;

    @Column(name = "higher_is_better", nullable = false)
    private Boolean higherIsBetter;

    protected Criterion() {

    }

    private Criterion(
        UUID id,
        UUID decisionId,
        String name,
        Float weight,
        String unit,
        Boolean higherIsBetter
    ) {
        this.id = id;
        this.decisionId = decisionId;
        this.name = name;
        this.weight = weight;
        this.unit = unit;
        this.higherIsBetter = higherIsBetter;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDecisionId() {
        return decisionId;
    }

    public String getName() {
        return name;
    }

    public Float getWeight() {
        return weight;
    }

    public String getUnit() {
        return unit;
    }

    public Boolean getHigherIsBetter() {
        return higherIsBetter;
    }

    public static Criterion create(
        UUID decisionId,
        String name,
        Float weight,
        String unit,
        Boolean higherIsBetter
    ) {
        return new Criterion(
            UUID.randomUUID(), 
            decisionId, 
            name, 
            weight, 
            unit, 
            higherIsBetter
        );
    }
}
