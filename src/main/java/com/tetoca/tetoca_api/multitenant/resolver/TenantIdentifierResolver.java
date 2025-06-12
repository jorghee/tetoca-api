package com.tetoca.tetoca_api.multitenant.resolver;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;

public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

  private static final String DEFAULT_TENANT = "public";

  @Override
  public String resolveCurrentTenantIdentifier() {
    String tenantId = TenantContextHolder.getTenantId();
    return (tenantId != null) ? tenantId : DEFAULT_TENANT;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }
}
