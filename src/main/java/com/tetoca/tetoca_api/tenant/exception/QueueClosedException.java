package com.tetoca.tetoca_api.tenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción específica de la lógica de negocio, lanzada cuando se intenta realizar
 * una operación en una cola que no está abierta.
 * Esta excepción se traduce automáticamente en una respuesta HTTP 409 (Conflict)
 * para indicar que la solicitud no se puede procesar debido al estado actual del recurso.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class QueueClosedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor con un mensaje de error.
   * @param message El mensaje que explica por qué la operación falló.
   */
  public QueueClosedException(String message) { super(message); }

  /**
   * Constructor con un mensaje de error y la causa original.
   * @param message El mensaje que explica el error.
   * @param cause La excepción original que causó este error.
   */
  public QueueClosedException(String message, Throwable cause) { super(message, cause); }
}
