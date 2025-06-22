package com.tetoca.tetoca_api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  // Manejador para excepciones de validación de DTOs (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    
    ErrorDetails errorDetails = new ErrorDetails(new Date(), "Error de Validación", request.getDescription(false), errors);
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  // Manejador para cualquier otra excepción no capturada
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false), null);
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  // DTO para la respuesta de error
  private static class ErrorDetails {
    public Date timestamp;
    public String message;
    public String details;
    public Map<String, String> validationErrors;
    
    public ErrorDetails(Date timestamp, String message, String details, Map<String, String> validationErrors) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.validationErrors = validationErrors;
    }
    
    // Getters para la serialización JSON
    public Date getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }
    public Map<String, String> getValidationErrors() { return validationErrors; }
  }
}
