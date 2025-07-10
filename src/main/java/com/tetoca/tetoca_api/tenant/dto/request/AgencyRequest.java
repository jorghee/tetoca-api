package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgencyRequest {

  @NotBlank(message = "El nombre de la agencia es obligatorio.")
  @Size(max = 100)
  private String name;

  @NotNull(message = "El ID de la división es obligatorio.")
  private Integer divisionId;

  @Size(max = 150)
  private String address;

  @Size(max = 100)
  private String reference;
}
