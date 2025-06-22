package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinQueueRequest {

  @NotBlank(message = "El token de notificación (pushToken) no puede estar vacío.")
  private String pushToken;
}
