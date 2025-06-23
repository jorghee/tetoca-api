package com.tetoca.tetoca_api.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidOAuthTokenException extends RuntimeException {

  public InvalidOAuthTokenException(String message) { super(message); }

  public InvalidOAuthTokenException(String message, Throwable cause) { super(message, cause); }
}
