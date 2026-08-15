package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;

import com.decisify.decision.dto.DecisionCreateRequest;
import com.decisify.decision.dto.DecisionResponse;
import com.decisify.decision.dto.DecisionUpdateRequest;

public interface DecisionService {
    DecisionResponse create(DecisionCreateRequest request);
    
    DecisionResponse update(UUID id, DecisionUpdateRequest request);

    DecisionResponse getById(UUID id);

    List<DecisionResponse> getAll();

    void delete(UUID id);
}
