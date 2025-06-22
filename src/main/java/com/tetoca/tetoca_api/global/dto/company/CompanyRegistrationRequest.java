package com.tetoca.tetoca_api.global.dto.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRegistrationRequest {

  @Valid
  @NotNull
  private CompanyRequest company;

  @Valid
  @NotNull
  private InstanceRequest instance;
}
