package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;
import com.decisify.decision.dto.CriterionResponse;

public interface CriterionService{
    CriterionResponse create(
        UUID decisionId,
        String name,
        Float weight,
        String unit,
        Boolean higherIsBetter
    );

    List<CriterionResponse> getByDecisionId(UUID decisionId);

    void delete(UUID decisionId, UUID criterionId);
}
