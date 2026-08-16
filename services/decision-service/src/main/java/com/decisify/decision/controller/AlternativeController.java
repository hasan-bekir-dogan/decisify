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
import com.decisify.decision.dto.AlternativeCreateRequest;
import com.decisify.decision.dto.AlternativeResponse;
import com.decisify.decision.service.AlternativeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/decisions/{decisionId}/alternatives")
public class AlternativeController {
    
    private final AlternativeService alternativeService;

    public AlternativeController(AlternativeService alternativeService) {
        this.alternativeService = alternativeService;
    }

    @PostMapping
    public ResponseEntity<AlternativeResponse> create(
        @PathVariable UUID decisionId,
        @Valid @RequestBody AlternativeCreateRequest request
    ) {

        AlternativeResponse response = alternativeService.create(decisionId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<AlternativeResponse> getAll(
        @PathVariable UUID decisionId
    ) {
        return alternativeService.getAllByDecisionId(decisionId);
    }

    @DeleteMapping("/{alternativeId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID decisionId,
        @PathVariable UUID alternativeId
    ) {
        alternativeService.delete(decisionId, alternativeId);

        return ResponseEntity.noContent().build();
    }
}
