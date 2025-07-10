package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DivisionRequest {

  @NotBlank(message = "El nombre de la división es obligatorio.")
  @Size(max = 100)
  private String name;

  @Size(max = 255)
  private String description;
}
