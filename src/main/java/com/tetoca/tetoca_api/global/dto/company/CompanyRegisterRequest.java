package com.tetoca.tetoca_api.global.dto.company;

import lombok.Data;

@Data
public class CompanyRegisterRequest {
  private String name;
  private String ruc;
  private String email;
  private Integer categoryCode;
  private Integer statusCode;

  // Instance
  private Integer dbTypeCode;
  private Integer connectionStateCode;
  private String dbName;
  private String dbUri;
  private String dbUser;
  private String dbPassword;
  private String tenantId;
}
