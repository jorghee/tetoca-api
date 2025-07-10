package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class QueueRequest {

  @NotBlank(message = "El nombre de la cola es obligatorio.")
  @Size(max = 100)
  private String name;

  @NotNull(message = "El ID de la agencia es obligatorio.")
  private Integer agencyId;

  @NotNull(message = "El ID del tipo de cola es obligatorio.")
  private Integer queueTypeId;

  @NotNull(message = "El ID del estado de la cola es obligatorio.")
  private Integer queueStatusId;

  private Integer maxCapacity;

  private Integer estimatedTimePerTurn; // En minutos

  @Valid
  private List<ScheduleRequest> schedules;
}
