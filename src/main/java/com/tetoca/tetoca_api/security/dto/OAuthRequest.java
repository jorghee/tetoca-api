package com.tetoca.tetoca_api.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthRequest {
  @NotBlank
  private String provider; // "google", "facebook", etc.
  @NotBlank
  private String token; // El token de id_token o access_token del proveedor
}
