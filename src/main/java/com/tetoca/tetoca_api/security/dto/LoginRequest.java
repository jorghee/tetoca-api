package com.tetoca.tetoca_api.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank
  private String username; // Será el email del trabajador o admin
  @NotBlank
  private String password;
}
