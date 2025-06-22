package com.tetoca.tetoca_api.tenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un operario intenta llamar a un turno,
 * pero no hay turnos en espera en la cola.
 * Se mapea a un HTTP 404 (Not Found) para indicar que no se encontró
 * el recurso "siguiente turno".
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoTurnsAvailableException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NoTurnsAvailableException(String message) { super(message); }

  public NoTurnsAvailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
