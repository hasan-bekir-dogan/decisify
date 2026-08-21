package com.decisify.decision.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.decisify.decision.dto.DecisionCalculationResponse;
import com.decisify.decision.dto.DecisionCreateRequest;
import com.decisify.decision.dto.DecisionResponse;
import com.decisify.decision.dto.DecisionUpdateRequest;
import com.decisify.decision.dto.RecommendationResponse;
import com.decisify.decision.service.DecisionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/decisions")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    public ResponseEntity<DecisionResponse> create(
            @Valid @RequestBody DecisionCreateRequest request) {
        DecisionResponse response = decisionService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public DecisionResponse getById(
            @PathVariable UUID id) {
        return decisionService.getById(id);
    }

    @PutMapping("/{id}")
    public DecisionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody DecisionUpdateRequest request) {
        return decisionService.update(id, request);
    }

    @GetMapping
    public List<DecisionResponse> getAll() {
        return decisionService.getAll();
    }

    @PostMapping("/{id}/calculate")
    public DecisionCalculationResponse calculate(
            @PathVariable UUID id) {
        return decisionService.calculate(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DecisionResponse> delete(
            @PathVariable UUID id) {
        decisionService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/recommendation")
    public RecommendationResponse getRecommendation(
        @PathVariable UUID id
    ) {
        return decisionService.getRecommendation(id);
    }
}
