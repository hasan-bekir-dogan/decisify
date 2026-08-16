package com.decisify.decision.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alternatives")
public class Alternative {
    
    @Id
    private UUID id;

    @Column(name = "decision_id", nullable = false)
    private UUID decisionId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected Alternative() {

    }

    private Alternative(
        UUID id,
        UUID decisionId,
        String name,
        String description
    ) {
        this.id = id;
        this.decisionId = decisionId;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public static Alternative create(
        UUID decisionId,
        String name,
        String description
    ) {
        return new Alternative(
            UUID.randomUUID(), 
            decisionId, 
            name, 
            description
        );
    }
}
