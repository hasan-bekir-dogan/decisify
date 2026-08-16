package com.decisify.decision.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.decisify.decision.domain.Decision;
import com.decisify.decision.dto.DecisionCreateRequest;
import com.decisify.decision.dto.DecisionResponse;
import com.decisify.decision.dto.DecisionUpdateRequest;
import com.decisify.decision.exception.DecisionNotFoundException;
import com.decisify.decision.repository.DecisionRepository;

@Service
public class DecisionServiceImpl implements DecisionService{
    
    private final DecisionRepository decisionRepository;

    public DecisionServiceImpl(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    @Override
    public DecisionResponse create(DecisionCreateRequest request) {

        Decision decision = Decision.create(
            request.userId(), 
            request.title(), 
            request.description()
        );

        Decision savedDecision = decisionRepository.save(decision);

        return toResponse(savedDecision);
    }

    @Override
    public DecisionResponse update(UUID id, DecisionUpdateRequest decisionUpdateRequest) {
        Decision decision = getDecisionById(id);

        decision.update(
            decisionUpdateRequest.title(),
            decisionUpdateRequest.description()
        );

        return toResponse(decision);
    }

    @Override
    public DecisionResponse getById(UUID id) {
        Decision decision = getDecisionById(id);

        return toResponse(decision);
    }

    @Override
    public List<DecisionResponse> getAll() {
        return decisionRepository.findAll()
            .stream()
            .map(decision -> toResponse(decision))
            .toList();
    }

    @Override
    public void delete(UUID id) {
        Decision decision = getDecisionById(id);

        decisionRepository.delete(decision);
    }

    private Decision getDecisionById(UUID id) {
        return decisionRepository.findById(id).orElseThrow(() -> 
            new DecisionNotFoundException(id)
        );
    }

    private DecisionResponse toResponse(Decision decision) {
        return new DecisionResponse(
            decision.getId(), 
            decision.getUserId(), 
            decision.getTitle(), 
            decision.getDescription(), 
            decision.getStatus(), 
            decision.getCreatedAt(), 
            decision.getUpdatedAt()
        );
    }
}
