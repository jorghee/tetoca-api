package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkerCreateRequest {

  @NotBlank(message = "El nombre completo es obligatorio.")
  @Size(max = 100)
  private String fullName;

  @NotBlank(message = "El email es obligatorio.")
  @Email(message = "El formato del email no es válido.")
  @Size(max = 100)
  private String email;

  @NotBlank(message = "La contraseña es obligatoria.")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
  private String password;

  @NotNull(message = "El tipo de trabajador es obligatorio.")
  private Integer workerTypeId;

  @Size(max = 15)
  private String phone;
}
