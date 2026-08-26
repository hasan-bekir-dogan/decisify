package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;

import com.decisify.decision.dto.CriterionValueResponse;

public interface CriterionValueService {

    CriterionValueResponse upsert(
        UUID decisionId,
        UUID alternativeId,
        UUID criterionId,
        Float rawValue
    );

    List<CriterionValueResponse> getByDecisionId(UUID decisionId);
}