package com.decisify.decision.repository;

import com.decisify.decision.domain.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CriterionRepository extends JpaRepository<Criterion, UUID> {

    List<Criterion> findByDecisionId(UUID decisionId);

    long countByDecisionId(UUID decisionId);

    boolean existsByDecisionIdAndNameIgnoreCase(UUID decisionId, String name);
}