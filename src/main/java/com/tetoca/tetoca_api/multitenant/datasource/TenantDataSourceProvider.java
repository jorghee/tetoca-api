package com.tetoca.tetoca_api.multitenant.datasource;

import com.tetoca.tetoca_api.global.model.Instance;
import com.tetoca.tetoca_api.global.repository.InstanceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TenantDataSourceProvider {

  private static final String DEFAULT_TENANT_KEY = "default";
  private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
  private final ApplicationContext applicationContext;
  private final DataSource globalDataSource;

  public TenantDataSourceProvider(
      ApplicationContext applicationContext,
      @Qualifier("globalDataSource") DataSource globalDataSource) {
    this.applicationContext = applicationContext;
    this.globalDataSource = globalDataSource;
  }

  private InstanceRepository getInstanceRepository() {
    return applicationContext.getBean(InstanceRepository.class);
  }

  public DataSource getDataSource(String tenantId) {
    if (DEFAULT_TENANT_KEY.equals(tenantId)) return globalDataSource;
    return dataSources.computeIfAbsent(tenantId, this::createAndCacheDataSource);
  }

  public void addTenant(String tenantId) {
    if (!dataSources.containsKey(tenantId)) {
      log.info("Dynamically adding new tenant: {}", tenantId);
      getDataSource(tenantId);
    }
  }

  public void removeTenant(String tenantId) {
    DataSource ds = dataSources.remove(tenantId);
    if (ds instanceof HikariDataSource) {
      log.info("Closing connection pool for tenant: {}", tenantId);
      ((HikariDataSource) ds).close();
    }
  }

  public void destroy() {
    dataSources.values().forEach(ds -> {
      if (ds instanceof HikariDataSource) {
        ((HikariDataSource) ds).close();
      }
    });
    dataSources.clear();
  }

  private DataSource createAndCacheDataSource(String tenantId) {
    log.info("No DataSource found for tenant '{}'. Attempting to create a new one.", tenantId);

    Instance instance = getInstanceRepository().findByTenantIdIgnoreCase(tenantId)
      .orElseThrow(() -> new RuntimeException("Tenant not found and cannot create DataSource: " + tenantId));
    
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(instance.getDbUri());
    config.setUsername(instance.getDbUser());
    config.setPassword(instance.getDbPassword());
    config.setDriverClassName("org.postgresql.Driver");
    config.setMaximumPoolSize(5);
    config.setMinimumIdle(1);
    config.setIdleTimeout(300000); // 5min
    config.setConnectionTimeout(30000); // 30s
    config.setPoolName("HikariPool-" + instance.getTenantId());

    log.info("DataSource created successfully for tenant '{}'", tenantId);
    return new HikariDataSource(config);
  }
}
