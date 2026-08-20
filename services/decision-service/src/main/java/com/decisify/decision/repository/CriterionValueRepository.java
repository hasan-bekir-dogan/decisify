package com.decisify.decision.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.CriterionValue;

public interface CriterionValueRepository extends JpaRepository<CriterionValue, UUID> {

    Optional<CriterionValue> findByAlternativeIdAndCriterionId(
            UUID alternativeId,
            UUID criterionId
    );

    List<CriterionValue> findByCriterionId(UUID criterionId);

    List<CriterionValue> findByAlternativeId(UUID alternativeId);
}