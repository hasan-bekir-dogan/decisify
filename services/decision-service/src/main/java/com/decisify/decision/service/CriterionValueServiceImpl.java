package com.decisify.decision.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.decisify.decision.domain.Alternative;
import com.decisify.decision.domain.Criterion;
import com.decisify.decision.domain.CriterionValue;
import com.decisify.decision.dto.CriterionValueResponse;
import com.decisify.decision.exception.AlternativeNotFoundException;
import com.decisify.decision.exception.CriterionNotFoundException;
import com.decisify.decision.exception.DecisionNotFoundException;
import com.decisify.decision.repository.AlternativeRepository;
import com.decisify.decision.repository.CriterionRepository;
import com.decisify.decision.repository.CriterionValueRepository;
import com.decisify.decision.repository.DecisionRepository;

@Service
public class CriterionValueServiceImpl implements CriterionValueService{
    
    private final CriterionRepository criterionRepository;
    private final CriterionValueRepository criterionValueRepository;
    private final AlternativeRepository alternativeRepository;
    private final DecisionRepository decisionRepository;


    public CriterionValueServiceImpl(
        CriterionValueRepository criterionValueRepository,
        CriterionRepository criterionRepository,
        DecisionRepository decisionRepository,
        AlternativeRepository alternativeRepository
    ) {
        this.criterionRepository = criterionRepository;
        this.criterionValueRepository = criterionValueRepository;
        this.alternativeRepository = alternativeRepository;
        this.decisionRepository = decisionRepository;
    }

    @Override
    public CriterionValueResponse upsert(
        UUID decisionId,
        UUID alternativeId,
        UUID criterionId,
        Float rawValue
    ) {
        if (!decisionRepository.existsById(decisionId)) {
            throw new DecisionNotFoundException(decisionId);
        }

        Alternative alternative = alternativeRepository.findById(alternativeId)
                .orElseThrow(() -> new AlternativeNotFoundException(alternativeId));

        if (!alternative.getDecisionId().equals(decisionId)) {
            throw new AlternativeNotFoundException(alternativeId);
        }

        Criterion criterion = criterionRepository.findById(criterionId)
                .orElseThrow(() -> new CriterionNotFoundException(criterionId));

        if (!criterion.getDecisionId().equals(decisionId)) {
            throw new CriterionNotFoundException(criterionId);
        }

        CriterionValue criterionValue =
                criterionValueRepository
                        .findByAlternativeIdAndCriterionId(
                                alternativeId,
                                criterionId
                        )
                        .map(existingValue -> {
                            existingValue.updateRawValue(
                                    rawValue,
                                    "MANUAL",
                                    null
                            );
                            return existingValue;
                        })
                        .orElseGet(() ->
                                CriterionValue.create(
                                        alternativeId,
                                        criterionId,
                                        rawValue,
                                        "MANUAL",
                                        null
                                )
                        );

        CriterionValue savedValue =
                criterionValueRepository.save(criterionValue);

        return toResponse(savedValue);
    }

    private CriterionValueResponse toResponse(CriterionValue criterionValue) {
        return new CriterionValueResponse(
            criterionValue.getId(),
            criterionValue.getAlternativeId(),
            criterionValue.getCriterionId(),
            criterionValue.getRawValue(),
            criterionValue.getNormalizedValue(),
            criterionValue.getSource(),
            criterionValue.getConfidence()
        );
    }
}
