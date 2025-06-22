package com.tetoca.tetoca_api.global.dto.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRegistrationRequest {

  @Valid
  @NotNull(message = "Los datos de la empresa son obligatorios.")
  private CompanyRequest company;

  @Valid
  @NotNull(message = "Los datos de la instancia son obligatorios.")
  private InstanceRequest instance;
}
