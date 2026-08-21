package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;

import com.decisify.decision.dto.DecisionCalculationResponse;
import com.decisify.decision.dto.DecisionCreateRequest;
import com.decisify.decision.dto.DecisionResponse;
import com.decisify.decision.dto.DecisionUpdateRequest;
import com.decisify.decision.dto.RecommendationResponse;

public interface DecisionService {
    DecisionResponse create(DecisionCreateRequest request);
    
    DecisionResponse update(UUID id, DecisionUpdateRequest request);

    DecisionResponse getById(UUID id);

    List<DecisionResponse> getAll();

    void delete(UUID id);

    DecisionCalculationResponse calculate(UUID decisionId);

    RecommendationResponse getRecommendation(UUID decisionId);
}
