package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.decisify.decision.domain.Alternative;
import com.decisify.decision.dto.AlternativeCreateRequest;
import com.decisify.decision.dto.AlternativeResponse;
import com.decisify.decision.exception.AlternativeNotFoundException;
import com.decisify.decision.exception.DecisionNotFoundException;
import com.decisify.decision.repository.AlternativeRepository;
import com.decisify.decision.repository.DecisionRepository;

@Service
public class AlternativeServiceImpl implements AlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final DecisionRepository decisionRepository;

    public AlternativeServiceImpl(
        AlternativeRepository alternativeRepository,
        DecisionRepository decisionRepository
    ) {
        this.alternativeRepository = alternativeRepository;
        this.decisionRepository = decisionRepository;
    }

    @Override
    public AlternativeResponse create(
        UUID decisionId, 
        AlternativeCreateRequest request
    ) {
        if (!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);

        Alternative alternative = Alternative.create(
            decisionId,
            request.name(),
            request.description()
        );

        Alternative savedAlternative = alternativeRepository.save(alternative);

        return toResponse(savedAlternative);
    }

    @Override
    public List<AlternativeResponse> getAllByDecisionId(UUID decisionId) {
        if (!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);

        return alternativeRepository.findByDecisionId(decisionId)
            .stream()
            .map(alternative -> toResponse(alternative))
            .toList();
    }

    @Override
    public void delete(UUID decisionId, UUID alternativeId) {
        if (!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);

        Alternative alternative = findById(alternativeId);

        if(!alternative.getDecisionId().equals(decisionId))
            throw new AlternativeNotFoundException(alternativeId);

        alternativeRepository.delete(alternative);
    }

    @Override
    public long countByDecisionId(UUID decisionId) {
        if(!decisionRepository.existsById(decisionId))
            throw new DecisionNotFoundException(decisionId);

        return alternativeRepository.countByDecisionId(decisionId);
    }

    private Alternative findById(UUID id) {
        return alternativeRepository.findById(id).orElseThrow(() -> 
            new AlternativeNotFoundException(id)
        );
    }

    private AlternativeResponse toResponse(Alternative alternative) {
        return new AlternativeResponse(
            alternative.getId(),
            alternative.getDecisionId(),
            alternative.getName(),
            alternative.getDescription()
        );
    }
}
