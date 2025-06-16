package com.tetoca.tetoca_api.tenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        HttpStatus status = determineStatus(ex.getMessage());
        return ResponseEntity.status(status)
                .body(Map.of("message", ex.getMessage()));
    }
    
    private HttpStatus determineStatus(String message) {
        if (message.contains("no encontrada") || message.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        }
        if (message.contains("no se puede") || message.contains("ya tiene")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}