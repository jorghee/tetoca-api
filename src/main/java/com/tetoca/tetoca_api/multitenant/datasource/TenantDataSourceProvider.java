package com.tetoca.tetoca_api.multitenant.datasource;

import com.tetoca.tetoca_api.global.model.InstanceEntity;
import com.tetoca.tetoca_api.global.repository.InstanceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TenantDataSourceProvider {

  @Autowired
  private ApplicationContext applicationContext;

  // Thread-safe storage of tenant-specific DataSources
  private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
  private volatile boolean initialized = false;

  private InstanceRepository getInstanceRepository() {
    return applicationContext.getBean(InstanceRepository.class);
  }

  public DataSource getDataSource(String tenantId) {
    if ("default".equals(tenantId)) return createDefaultDataSource();

    return dataSources.computeIfAbsent(tenantId, this::loadDataSourceForTenant);
  }

  public synchronized void loadInitialTenants() {
    if (!initialized) {
      try {
        getInstanceRepository().findAll().forEach(this::createAndStoreDataSource);
        initialized = true;
      } catch (Exception e) {
        System.err.println("Error loading initial tenants: {}" + e.getMessage());
      }
    }
  }

  private DataSource loadDataSourceForTenant(String tenantId) {
    InstanceEntity instance = getInstanceRepository().findByTenantIdIgnoreCase(tenantId)
      .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
    return createAndStoreDataSource(instance);
  }

  private DataSource createAndStoreDataSource(InstanceEntity instance) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(instance.getDbUri());
    config.setUsername(instance.getDbUser());
    config.setPassword(instance.getDbPassword());
    config.setDriverClassName("org.postgresql.Driver");
    config.setMaximumPoolSize(5);
    config.setMinimumIdle(1);
    config.setIdleTimeout(300000);
    config.setConnectionTimeout(30000);
    config.setPoolName("Hikari-" + instance.getTenantId());

    HikariDataSource ds = new HikariDataSource(config);
    dataSources.put(instance.getTenantId(), ds);
    return ds;
  }

  private DataSource createDefaultDataSource() {
    // This should probably connect to a default database or throw an exception
    // For now, we'll throw an exception to make it explicit that no default is configured
    throw new IllegalStateException("No default tenant configured. Please provide a valid tenant ID.");
  }

  public void addTenant(String tenantId) {
    if (!dataSources.containsKey(tenantId)) loadDataSourceForTenant(tenantId);
  }

  public void removeTenant(String tenantId) {
    DataSource ds = dataSources.remove(tenantId);
    if (ds instanceof HikariDataSource) ((HikariDataSource) ds).close();
  }

  public void destroy() {
    dataSources.values().forEach(ds -> {
      if (ds instanceof HikariDataSource) ((HikariDataSource) ds).close();
    });
    dataSources.clear();
  }
}
