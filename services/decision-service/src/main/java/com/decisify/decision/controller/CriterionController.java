package com.decisify.decision.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.decisify.decision.dto.CriterionCreateRequest;
import com.decisify.decision.dto.CriterionResponse;
import com.decisify.decision.service.CriterionService;

@RestController
@RequestMapping("/decisions/{decisionId}/criteria")
public class CriterionController {
    
    private final CriterionService criterionService;

    public CriterionController(CriterionService criterionService) {
        this.criterionService = criterionService;
    }

    @PostMapping
    public ResponseEntity<CriterionResponse> create(
        @PathVariable UUID decisionId,
        @RequestBody CriterionCreateRequest request
    ) {
        CriterionResponse response = criterionService.create(
            decisionId, 
            request.name(), 
            request.weight(), 
            request.unit(), 
            request.higherIsBetter()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    public List<CriterionResponse> getAll(
        @PathVariable UUID decisionId
    ) {
        return criterionService.getByDecisionId(decisionId);
    }

    @DeleteMapping("/{criterionId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID decisionId,
        @PathVariable UUID criterionId
    ) {
        criterionService.delete(decisionId, criterionId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
