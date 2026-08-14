package com.decisify.decision.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.Criterion;

public interface CriterionRepository extends JpaRepository<Criterion, UUID>{
    
}
