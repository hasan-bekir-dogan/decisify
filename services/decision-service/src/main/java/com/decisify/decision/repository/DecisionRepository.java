package com.decisify.decision.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.Decision;

public interface DecisionRepository extends JpaRepository<Decision, UUID>{
    
}
