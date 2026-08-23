package com.decisify.auth.exception;

import com.decisify.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
                EmailAlreadyExistsException exception
        ) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        exception.getMessage(),
                        OffsetDateTime.now());

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                MethodArgumentNotValidException exception
        ) {
                String message = exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .orElse("Validation failed");

                ErrorResponse response = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        message,
                        OffsetDateTime.now());

                return ResponseEntity
                        .badRequest()
                        .body(response);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentials(
                InvalidCredentialsException exception
        ) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        exception.getMessage(),
                        OffsetDateTime.now());

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
        }

        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(
                InvalidRefreshTokenException exception
        ) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        exception.getMessage(),
                        OffsetDateTime.now());

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFound(
                UserNotFoundException exception
        ) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "User not found",
                        OffsetDateTime.now()
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
        }
}