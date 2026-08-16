package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;

import com.decisify.decision.dto.AlternativeCreateRequest;
import com.decisify.decision.dto.AlternativeResponse;

public interface AlternativeService {
    AlternativeResponse create(UUID decisionId, AlternativeCreateRequest request);
    List<AlternativeResponse> getAllByDecisionId(UUID decisionId);
    void delete(UUID decisionId, UUID alternativeId);
    long countByDecisionId(UUID decisionId);
}
