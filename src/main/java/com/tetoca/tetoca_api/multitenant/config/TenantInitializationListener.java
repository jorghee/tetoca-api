package com.tetoca.tetoca_api.multitenant.config;

import com.tetoca.tetoca_api.multitenant.datasource.TenantDataSourceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener that initializes tenant DataSources after the application is fully ready
 * This ensures that the global database configuration is available before trying to
 * load tenant information from it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInitializationListener implements ApplicationListener<ApplicationReadyEvent> {
  
  private final TenantDataSourceProvider tenantDataSourceProvider;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    try {
      tenantDataSourceProvider.loadInitialTenants();
      System.out.println("Tenant DataSources initialized successfully");
    } catch (Exception e) {
      System.err.printf("Failed to initialize tenant DataSources: {}", e.getMessage(), e);
      // Depending on your requirements, you might want to:
      // - throw new RuntimeException(e) to fail application startup
      // - or just log and continue (tenants will be loaded on-demand)
    }
  }
}
