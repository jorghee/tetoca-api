package com.tetoca.tetoca_api.global.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRequest {
  @NotBlank(message = "El nombre de la empresa no puede estar vacío.")
  @Size(max = 100)
  private String name;

  @NotBlank(message = "El RUC no puede estar vacío.")
  @Size(min = 11, max = 11, message = "El RUC debe tener 11 dígitos.")
  private String ruc;

  @NotBlank(message = "El email no puede estar vacío.")
  @Email(message = "El formato del email no es válido.")
  private String email;

  @NotNull(message = "El código de categoría es obligatorio.")
  private Integer categoryCode;

  // No incluimos el estado, ya que debería ser gestionado internamente ('A' por defecto al crear).
}
