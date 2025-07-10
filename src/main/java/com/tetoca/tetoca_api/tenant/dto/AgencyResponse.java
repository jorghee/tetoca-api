package com.tetoca.tetoca_api.tenant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyResponse {
  private Integer id;
  private String name;
  private String address;
  private String reference;
  private String recordStatus;
  private Integer divisionId;
}
