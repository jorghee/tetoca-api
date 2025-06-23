package com.tetoca.tetoca_api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class OAuthStrategyFactory {
  private final Map<String, OAuthValidationService> strategies = new HashMap<>();

  @Autowired
  public OAuthStrategyFactory(Set<OAuthValidationService> strategySet) {
    strategySet.forEach(
      strategy -> strategies.put(strategy.getProviderName(), strategy)
    );
  }

  public OAuthValidationService findStrategy(String providerName) {
    OAuthValidationService service = strategies.get(providerName.toLowerCase());
    if (service == null) {
      throw new IllegalArgumentException("OAuth provider not supported: " + providerName);
    }
    return service;
  }
}
