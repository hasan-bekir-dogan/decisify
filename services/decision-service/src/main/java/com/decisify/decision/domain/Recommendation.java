package com.decisify.decision.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    
    @Id
    private UUID id;

    @Column(name = "decision_id", nullable = false)
    private UUID decisionId;

    @Column(name = "selected_alternative_id", nullable = false)
    private UUID selectedAlternativeId;

    @Column(name = "final_score", nullable = false)
    private Float finalScore;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Recommendation() {

    }

    public Recommendation(
        UUID id,
        UUID decisionId,
        UUID selectedAlternativeId,
        Float finalScore,
        String explanation,
        OffsetDateTime createdAt
    ) {
        this.id = id;
        this.decisionId = decisionId;
        this.selectedAlternativeId = selectedAlternativeId;
        this.finalScore = finalScore;
        this.explanation = explanation;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDecisionId() {
        return decisionId;
    }

    public UUID getSelectedAkternativeId() {
        return selectedAlternativeId;
    }

    public Float getFinalScore() {
        return finalScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
