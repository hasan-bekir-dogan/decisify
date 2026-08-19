package com.decisify.decision.controller;

import com.decisify.decision.dto.CriterionValueResponse;
import com.decisify.decision.dto.CriterionValueUpsertRequest;
import com.decisify.decision.service.CriterionValueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/decisions/{decisionId}/values")
public class CriterionValueController {

    private final CriterionValueService criterionValueService;

    public CriterionValueController(
            CriterionValueService criterionValueService
    ) {
        this.criterionValueService = criterionValueService;
    }

    @PostMapping
    public ResponseEntity<CriterionValueResponse> upsert(
            @PathVariable UUID decisionId,
            @Valid @RequestBody CriterionValueUpsertRequest request
    ) {
        CriterionValueResponse response = criterionValueService.upsert(
                decisionId,
                request.alternativeId(),
                request.criterionId(),
                request.rawValue()
        );

        return ResponseEntity.ok(response);
    }
}