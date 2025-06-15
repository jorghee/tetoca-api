package com.tetoca.tetoca_api.multitenant.datasource;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

  private final TenantDataSourceProvider tenantDataSourceProvider;

  public TenantRoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
    this.tenantDataSourceProvider = tenantDataSourceProvider;

    setDefaultTargetDataSource(tenantDataSourceProvider.getDataSource("default"));
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return TenantContextHolder.getTenantId();
  }

  @Override
  protected DataSource determineTargetDataSource() {
    String tenantId = (String) determineCurrentLookupKey();

    if (tenantId == null || tenantId.isBlank()) {
      throw new IllegalStateException("No tenant ID set in TenantContextHolder");
    }

    return tenantDataSourceProvider.getDataSource(tenantId);
  }
}
