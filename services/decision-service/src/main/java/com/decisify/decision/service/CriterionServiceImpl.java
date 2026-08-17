package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.decisify.decision.domain.Criterion;
import com.decisify.decision.dto.CriterionResponse;
import com.decisify.decision.exception.CriterionNotFoundException;
import com.decisify.decision.exception.DecisionNotFoundException;
import com.decisify.decision.exception.InvalidCriterionWeightException;
import com.decisify.decision.repository.CriterionRepository;
import com.decisify.decision.repository.DecisionRepository;

@Service
public class CriterionServiceImpl implements CriterionService{
    
    private final CriterionRepository criterionRepository;
    private final DecisionRepository decisionRepository;

    public CriterionServiceImpl(
        CriterionRepository criterionRepository, 
        DecisionRepository decisionRepository
    ) {
        this.criterionRepository = criterionRepository;
        this.decisionRepository = decisionRepository;
    }

    @Override
    public CriterionResponse create(
        UUID decisionId,
        String name,
        Float weight,
        String unit,
        Boolean highIsBetter
    ) {
        if(!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);

        float currentTotalWeight = criterionRepository.findByDecisionId(decisionId)
            .stream()
            .map(Criterion::getWeight)
            .reduce(0.0f, Float::sum);
            
        if (currentTotalWeight + weight > 100.0f) {
            throw new InvalidCriterionWeightException(
                currentTotalWeight,
                weight
            );
        }

        Criterion criterion = Criterion.create(
                                    decisionId, 
                                    name, 
                                    weight, 
                                    unit, 
                                    highIsBetter
                                );

        criterionRepository.save(criterion);

        return toResponse(criterion);
    }

    @Override
    public List<CriterionResponse> getByDecisionId(UUID decisionId) {
        if(!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);

        return criterionRepository.findByDecisionId(decisionId)
            .stream()
            .map(
                criterion -> toResponse(criterion)
            )
            .toList();
    }

    @Override
    public void delete(UUID decisionId, UUID criterionId) {
        if(!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);
        
        Criterion criterion = getByCriterionId(criterionId);

        if(!criterion.getDecisionId().equals(decisionId))
            throw new CriterionNotFoundException(criterionId);

        criterionRepository.delete(criterion);
    }

    private Criterion getByCriterionId(UUID criterionId) {
        return criterionRepository.findById(criterionId).orElseThrow(() ->
            new CriterionNotFoundException(criterionId)
        );
    }

    private CriterionResponse toResponse(Criterion criterion) {
        return new CriterionResponse(
            criterion.getId(), 
            criterion.getDecisionId(), 
            criterion.getName(), 
            criterion.getWeight(), 
            criterion.getUnit(), 
            criterion.getHigherIsBetter()
        );
    }
}
