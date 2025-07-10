package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OperatorAssignmentRequest {

  @NotNull(message = "El ID del trabajador (operario) es obligatorio.")
  private Integer workerId;

  @NotNull(message = "El ID de la cola es obligatorio.")
  private Integer queueId;
}
