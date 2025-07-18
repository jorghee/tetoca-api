package com.tetoca.tetoca_api.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthUserInfo {
  private String externalUid;
  private String email;
  private String fullName;
  private String provider;
}
