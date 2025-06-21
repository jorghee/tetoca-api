package com.tetoca.tetoca_api.multitenant.datasource;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

  private final TenantDataSourceProvider tenantDataSourceProvider;

  public TenantRoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
    this.tenantDataSourceProvider = tenantDataSourceProvider;
  }

  @Override
  protected Object determineCurrentLookupKey() {
    String tenantId = TenantContextHolder.getTenantId();
    return (tenantId != null) ? tenantId : "default";
  }

  @Override
  protected DataSource determineTargetDataSource() {
    String tenantId = (String) determineCurrentLookupKey();
    return tenantDataSourceProvider.getDataSource(tenantId);
  }

  @Override
  public void afterPropertiesSet() {}
}
