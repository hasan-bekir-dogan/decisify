package com.decisify.decision.service;

import com.decisify.decision.domain.Criterion;
import com.decisify.decision.domain.CriterionValue;
import com.decisify.decision.exception.CriterionNotFoundException;
import com.decisify.decision.repository.CriterionRepository;
import com.decisify.decision.repository.CriterionValueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NormalizationServiceImpl implements NormalizationService {

    private final CriterionRepository criterionRepository;
    private final CriterionValueRepository criterionValueRepository;

    public NormalizationServiceImpl(
            CriterionRepository criterionRepository,
            CriterionValueRepository criterionValueRepository
    ) {
        this.criterionRepository = criterionRepository;
        this.criterionValueRepository = criterionValueRepository;
    }

    @Override
    public void normalize(UUID criterionId) {

        Criterion criterion = criterionRepository.findById(criterionId)
            .orElseThrow(() -> new CriterionNotFoundException(criterionId));

        List<CriterionValue> values = criterionValueRepository.findByCriterionId(criterionId);
        
        if (values.isEmpty())
            return;

        float min = values.stream()
            .map(CriterionValue::getRawValue)
            .min(Float::compare)
            .orElseThrow();

        float max = values.stream()
            .map(CriterionValue::getRawValue)
            .max(Float::compare)
            .orElseThrow();
        
        // All values are equal, so every alternative is equally good for this criterion.
        // Assign 1.0 to avoid division by zero during min-max normalization.
        if (Float.compare(min, max) == 0) {
            values.forEach(value -> value.updateNormalizedValue(1.0f));
            criterionValueRepository.saveAll(values);
            return;
        }

        for (CriterionValue value : values) {
            float normalized;

            if (criterion.getHigherIsBetter()) {
                // Higher raw values should produce higher normalized scores.
                normalized = (value.getRawValue() - min) / (max - min);
            } else {
                // Lower raw values should produce higher normalized scores.
                normalized = (max - value.getRawValue()) / (max - min);
            }

            value.updateNormalizedValue(normalized);
        }

        criterionValueRepository.saveAll(values);

    }
}