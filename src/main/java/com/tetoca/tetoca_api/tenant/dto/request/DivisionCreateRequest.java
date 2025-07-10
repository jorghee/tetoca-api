package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DivisionCreateRequest {

  @Valid
  @NotNull(message = "Los datos de la división son obligatorios.")
  private DivisionRequest division;

  @Valid
  @NotNull(message = "Los datos del administrador a crear son obligatorios.")
  private WorkerCreateRequest admin;
}
