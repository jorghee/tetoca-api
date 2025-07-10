package com.tetoca.tetoca_api.tenant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class WorkerResponse {
  private Integer id;
  private String fullName;
  private String email;
  private String phone;
  private String workerType;
  private String recordStatus;
  private Set<String> roles;
}
