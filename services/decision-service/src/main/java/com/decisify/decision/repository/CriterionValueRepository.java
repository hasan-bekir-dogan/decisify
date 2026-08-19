package com.decisify.decision.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.CriterionValue;

public interface CriterionValueRepository extends JpaRepository<CriterionValue, UUID>{
    Optional<CriterionValue> findByAlternativeIdAndCriterionId(
        UUID alternativeId, 
        UUID criterionId
    );
}
