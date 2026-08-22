package com.decisify.decision.service;

import com.decisify.decision.domain.Criterion;
import com.decisify.decision.domain.CriterionValue;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalizationServiceImplTest {
    
    @Mock
    private CriterionRepository criterionRepository;

    @Mock
    private CriterionValueRepository criterionValueRepository;

    private NormalizationServiceImpl normalizationService;

    private UUID criterionId;

    @BeforeEach
    void setUp() {
        normalizationService = new NormalizationServiceImpl(
            criterionRepository,
            criterionValueRepository
        );

        criterionId = UUID.randomUUID();
    }

    @Test
    void shouldNormalizeHigherIsBetterCriterion() {
        Criterion criterion = Criterion.create(
            UUID.randomUUID(), 
            "Performance", 
            50.0f, 
            "score", 
            true
        );

        CriterionValue low = CriterionValue.create(
            UUID.randomUUID(), 
            criterionId, 
            10.0f, 
            "test", 
            1.0f
        );

        CriterionValue middle = CriterionValue.create(
            UUID.randomUUID(), 
            criterionId, 
            20.0f, 
            "test", 
            1.0f
        );

        CriterionValue high = CriterionValue.create(
            UUID.randomUUID(), 
            criterionId, 
            30.0f, 
            "test", 
            1.0f
        );

        when(criterionRepository.findById(criterionId))
            .thenReturn(Optional.of(criterion));
        
        when(criterionValueRepository.findByCriterionId(criterionId))
            .thenReturn(List.of(low, middle, high));
        
        normalizationService.normalize(criterionId);

        assertEquals(0.0f, low.getNormalizedValue(), 0.0001f);
        assertEquals(0.5f, middle.getNormalizedValue(), 0.0001f);
        assertEquals(1.0f, high.getNormalizedValue(), 0.0001f);

        verify(criterionValueRepository)
            .saveAll(List.of(low, middle, high));
    }

    @Test
    void shouldNormalizeLowerIsBetterCriterion() {
        Criterion criterion = Criterion.create(
                UUID.randomUUID(),
                "Cost",
                50.0f,
                "EUR",
                false
        );

        CriterionValue low = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                10.0f,
                "test",
                1.0f
        );

        CriterionValue middle = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                20.0f,
                "test",
                1.0f
        );

        CriterionValue high = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                30.0f,
                "test",
                1.0f
        );

        when(criterionRepository.findById(criterionId))
                .thenReturn(Optional.of(criterion));

        when(criterionValueRepository.findByCriterionId(criterionId))
                .thenReturn(List.of(low, middle, high));

        normalizationService.normalize(criterionId);

        assertEquals(1.0f, low.getNormalizedValue(), 0.0001f);
        assertEquals(0.5f, middle.getNormalizedValue(), 0.0001f);
        assertEquals(0.0f, high.getNormalizedValue(), 0.0001f);

        verify(criterionValueRepository)
                .saveAll(List.of(low, middle, high));
    }

    @Test
    void shouldNormalizeAllEqualValuesToOne() {
        Criterion criterion = Criterion.create(
                UUID.randomUUID(),
                "Performance",
                50.0f,
                "score",
                true
        );

        CriterionValue first = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                50.0f,
                "test",
                1.0f
        );

        CriterionValue second = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                50.0f,
                "test",
                1.0f
        );

        CriterionValue third = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                50.0f,
                "test",
                1.0f
        );

        when(criterionRepository.findById(criterionId))
                .thenReturn(Optional.of(criterion));

        when(criterionValueRepository.findByCriterionId(criterionId))
                .thenReturn(List.of(first, second, third));

        normalizationService.normalize(criterionId);

        assertEquals(1.0f, first.getNormalizedValue(), 0.0001f);
        assertEquals(1.0f, second.getNormalizedValue(), 0.0001f);
        assertEquals(1.0f, third.getNormalizedValue(), 0.0001f);

        verify(criterionValueRepository)
                .saveAll(List.of(first, second, third));
    }

    @Test
    void shouldNormalizeSingleValueToOne() {
        Criterion criterion = Criterion.create(
                UUID.randomUUID(),
                "Performance",
                50.0f,
                "score",
                true
        );

        CriterionValue value = CriterionValue.create(
                UUID.randomUUID(),
                criterionId,
                75.0f,
                "test",
                1.0f
        );

        when(criterionRepository.findById(criterionId))
                .thenReturn(Optional.of(criterion));

        when(criterionValueRepository.findByCriterionId(criterionId))
                .thenReturn(List.of(value));

        normalizationService.normalize(criterionId);

        assertEquals(1.0f, value.getNormalizedValue(), 0.0001f);

        verify(criterionValueRepository)
                .saveAll(List.of(value));
    }
}
