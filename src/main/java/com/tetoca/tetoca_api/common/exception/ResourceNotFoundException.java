package com.tetoca.tetoca_api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se puede encontrar un recurso específico en la base de datos.
 * Esta excepción se traduce automáticamente en una respuesta HTTP 404 (Not Found)
 * gracias a la anotación @ResponseStatus.
 */
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String resourceName;
  private final String fieldName;
  private final transient Object fieldValue;

  /**
   * Constructor para crear un mensaje de error detallado.
   * @param resourceName El nombre del recurso que no se encontró (e.g., "Queue", "Turn").
   * @param fieldName El nombre del campo por el que se buscó (e.g., "id", "name").
   * @param fieldValue El valor del campo que se buscó (e.g., 123, "VIP").
   */
  public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
    super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }

  /**
   * Constructor simple con un mensaje personalizado.
   * @param message El mensaje de error.
   */
  public ResourceNotFoundException(String message) {
    super(message);
    this.resourceName = null;
    this.fieldName = null;
    this.fieldValue = null;
  }
}
