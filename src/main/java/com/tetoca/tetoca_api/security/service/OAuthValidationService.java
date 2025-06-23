package com.tetoca.tetoca_api.security.service;

import com.tetoca.tetoca_api.security.dto.OAuthUserInfo;

public interface OAuthValidationService {
  OAuthUserInfo validateAndExtractUserInfo(String token);
  String getProviderName();
}
