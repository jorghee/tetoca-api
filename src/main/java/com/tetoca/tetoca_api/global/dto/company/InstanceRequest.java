package com.tetoca.tetoca_api.global.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstanceRequest {

  @NotBlank(message = "El tenantId no puede estar vacío.")
  private String tenantId;

  @NotBlank(message = "El nombre de la base de datos no puede estar vacío.")
  private String dbName;
  
  @NotBlank
  private String dbUri;

  @NotBlank
  private String dbUser;

  @NotBlank
  private String dbPassword;

  @NotNull(message = "El tipo de base de datos es obligatorio.")
  private Integer dbTypeCode;
}
