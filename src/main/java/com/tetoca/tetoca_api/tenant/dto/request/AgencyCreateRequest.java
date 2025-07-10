package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgencyCreateRequest {

  @Valid
  @NotNull(message = "Los datos de la agencia son obligatorios.")
  private AgencyRequest agency;

  @Valid
  @NotNull(message = "Los datos del administrador a crear son obligatorios.")
  private WorkerCreateRequest admin;
}
