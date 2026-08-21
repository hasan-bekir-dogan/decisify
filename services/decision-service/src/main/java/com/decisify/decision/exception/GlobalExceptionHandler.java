package com.decisify.decision.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
        
        @ExceptionHandler(DecisionNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleDecisionNotFound(
                DecisionNotFoundException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Decision not found",
                                "message", exception.getMessage())
                        );
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationErrors(
                MethodArgumentNotValidException exception
        ) {
                Map<String, String> errors = new HashMap<>();

                exception.getBindingResult()
                        .getFieldErrors()
                        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", 400,
                                "errors", errors)
                        );
        }

        @ExceptionHandler(AlternativeNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleAlternativeNotFound(
                AlternativeNotFoundException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Alternative not found",
                                "message", exception.getMessage())
                        );
        }

        @ExceptionHandler(CriterionNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleCriterionNotFound(
                CriterionNotFoundException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Criterion not found",
                                "message", exception.getMessage()
                        ));
        }
        
        @ExceptionHandler(InvalidCriterionWeightException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidCriterionWeight(
                InvalidCriterionWeightException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", 400,
                                "error", "Invalid criterion weight",
                                "message", exception.getMessage()
                        ));
        }
        
        @ExceptionHandler(RecommendationNotFoundException.class)
        public ResponseEntity<?> handleRecommendationNotFound(
                RecommendationNotFoundException ex
        ) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", ex.getMessage()));
        }

        @ExceptionHandler(InvalidDecisionStateException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidDecisionState(
                InvalidDecisionStateException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", 400,
                                "error", "Invalid decision state",
                                "message", exception.getMessage()
                        ));
        }

        @ExceptionHandler(DuplicateCriterionException.class)
        public ResponseEntity<Map<String, Object>> handleDuplicateCriterion(
                DuplicateCriterionException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", 409,
                                "error", "Duplicate criterion",
                                "message", exception.getMessage()
                        ));
        }

        @ExceptionHandler(DuplicateAlternativeException.class)
        public ResponseEntity<Map<String, Object>> handleDuplicateAlternative(
                DuplicateAlternativeException exception
        ) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", 409,
                                "error", "Duplicate alternative",
                                "message", exception.getMessage()
                        ));
        }
}
