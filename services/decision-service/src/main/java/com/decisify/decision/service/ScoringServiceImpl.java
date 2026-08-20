package com.decisify.decision.service;

import com.decisify.decision.domain.Criterion;
import com.decisify.decision.domain.CriterionValue;
import com.decisify.decision.dto.AlternativeScoreResponse;
import com.decisify.decision.dto.CriterionContribution;
import com.decisify.decision.repository.CriterionRepository;
import com.decisify.decision.repository.CriterionValueRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ScoringServiceImpl implements ScoringService {

    private final CriterionRepository criterionRepository;
    private final CriterionValueRepository criterionValueRepository;

    public ScoringServiceImpl(
            CriterionRepository criterionRepository,
            CriterionValueRepository criterionValueRepository
    ) {
        this.criterionRepository = criterionRepository;
        this.criterionValueRepository = criterionValueRepository;
    }

    @Override
    public AlternativeScoreResponse calculateScore(UUID alternativeId) {

        List<CriterionValue> values = criterionValueRepository.findByAlternativeId(alternativeId);

        float totalScore = 0.0f;
        List<CriterionContribution> contributions = new ArrayList<>();

        for (CriterionValue value : values) {

            // Values that have not been normalized yet are excluded from scoring.
            if (value.getNormalizedValue() == null)
                continue;

            Criterion criterion = criterionRepository
                .findById(value.getCriterionId())
                .orElseThrow();

            float contribution = value.getNormalizedValue() * criterion.getWeight();

            totalScore += contribution;

            contributions.add(new CriterionContribution(
                criterion.getId(),
                criterion.getName(),
                value.getNormalizedValue(),
                criterion.getWeight(),
                contribution
            ));
        }

        return new AlternativeScoreResponse(
            alternativeId,
            totalScore,
            contributions
        );
    }
}