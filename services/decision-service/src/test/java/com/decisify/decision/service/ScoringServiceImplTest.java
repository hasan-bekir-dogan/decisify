package com.decisify.decision.service;

import com.decisify.decision.domain.Criterion;
import com.decisify.decision.domain.CriterionValue;
import com.decisify.decision.dto.AlternativeScoreResponse;
import com.decisify.decision.repository.CriterionRepository;
import com.decisify.decision.repository.CriterionValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringServiceImplTest {

    @Mock
    private CriterionRepository criterionRepository;

    @Mock
    private CriterionValueRepository criterionValueRepository;

    private ScoringServiceImpl scoringService;

    private UUID alternativeId;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringServiceImpl(
                criterionRepository,
                criterionValueRepository
        );

        alternativeId = UUID.randomUUID();
    }

    @Test
    void shouldCalculateWeightedScore() {
        UUID decisionId = UUID.randomUUID();

        Criterion cost = Criterion.create(
                decisionId,
                "Cost",
                40.0f,
                "EUR",
                false
        );

        Criterion performance = Criterion.create(
                decisionId,
                "Performance",
                60.0f,
                "score",
                true
        );

        CriterionValue costValue = CriterionValue.create(
                alternativeId,
                cost.getId(),
                100.0f,
                "test",
                1.0f
        );

        CriterionValue performanceValue = CriterionValue.create(
                alternativeId,
                performance.getId(),
                80.0f,
                "test",
                1.0f
        );

        costValue.updateNormalizedValue(0.8f);
        performanceValue.updateNormalizedValue(0.6f);

        when(criterionValueRepository.findByAlternativeId(alternativeId))
                .thenReturn(List.of(costValue, performanceValue));

        when(criterionRepository.findById(cost.getId()))
                .thenReturn(Optional.of(cost));

        when(criterionRepository.findById(performance.getId()))
                .thenReturn(Optional.of(performance));

        AlternativeScoreResponse result =
                scoringService.calculateScore(alternativeId);

        // Cost:        0.8 * 40 = 32
        // Performance: 0.6 * 60 = 36
        // Total:                  68
        assertEquals(68.0f, result.score(), 0.0001f);

        assertEquals(2, result.contributions().size());

        assertEquals(
                32.0f,
                result.contributions().get(0).contribution(),
                0.0001f
        );

        assertEquals(
                36.0f,
                result.contributions().get(1).contribution(),
                0.0001f
        );
    }
}