package com.tetoca.tetoca_api.global.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstanceRequest {

  @NotBlank(message = "El tenantId no puede estar vacío.")
  @Size(max = 50, message = "El tenantId no puede exceder los 50 caracteres.")
  private String tenantId;

  @NotBlank(message = "El nombre de la base de datos no puede estar vacío.")
  private String dbName;
  
  @NotBlank(message = "La URI de la base de datos no puede estar vacía.")
  private String dbUri;

  @NotBlank(message = "El usuario de la base de datos no puede estar vacío.")
  private String dbUser;

  @NotBlank(message = "La contraseña de la base de datos no puede estar vacía.")
  private String dbPassword;

  @NotNull(message = "El tipo de base de datos es obligatorio.")
  private Integer dbTypeCode;
}
