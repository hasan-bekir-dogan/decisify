package com.decisify.decision.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.Alternative;

public interface AlternativeRepository extends JpaRepository<Alternative, UUID>{
    List<Alternative> findByDecisionId(UUID decisionId);

    long countByDecisionId(UUID decisionId);

    boolean existsByDecisionIdAndNameIgnoreCase(UUID decisionId, String name);
}
