package com.tetoca.tetoca_api.global.dto.company;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
  private Integer id;
  private String name;
  private String ruc;
  private String email;
  private String category;
  private String state;
  private String tenantId;
  private String recordStatus;
  private Integer registerDate;
}
