package com.test.edc.taskmanager.task_manager_api.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null
                                ? "Valeur invalide"
                                : error.getDefaultMessage(),
                        (first, second) -> first
                ));

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation échouée",
                "details", errors
        ));
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(
            org.springframework.web.server.ResponseStatusException ex) {

        int status = ex.getStatusCode().value();

        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "status", status,
                "error", HttpStatus.valueOf(status).getReasonPhrase(),
                "message", ex.getReason() == null
                        ? "Une erreur est survenue"
                        : ex.getReason()
        ));
    }
}