package com.tetoca.tetoca_api.tenant.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleRequest {

  @NotNull(message = "El día de la semana es obligatorio.")
  @Min(value = 1, message = "El día debe ser entre 1 (Lunes) y 7 (Domingo).")
  @Max(value = 7, message = "El día debe ser entre 1 (Lunes) y 7 (Domingo).")
  private Integer dayOfWeek; // 1=Lunes, 7=Domingo

  @NotNull(message = "La hora de inicio es obligatoria.")
  private Integer startTime; // Formato HHMM, 900 para 09:00, 1800 para 18:00

  @NotNull(message = "La hora de fin es obligatoria.")
  private Integer endTime;
}
