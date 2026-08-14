package com.decisify.decision.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID>{
    
}
