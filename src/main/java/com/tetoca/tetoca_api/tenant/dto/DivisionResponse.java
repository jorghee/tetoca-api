package com.tetoca.tetoca_api.tenant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DivisionResponse {
  private Integer id;
  private String name;
  private String description;
  private String recordStatus;
}
