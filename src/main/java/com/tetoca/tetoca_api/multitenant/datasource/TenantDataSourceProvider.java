package com.tetoca.tetoca_api.multitenant.datasource;

import com.tetoca.tetoca_api.global.entity.InstanceEntity;
import com.tetoca.tetoca_api.global.repository.InstanceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TenantDataSourceProvider {

  private final InstanceRepository instanceRepository;

  // Thread-safe storage of tenant-specific DataSources
  private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  @PostConstruct
  public void loadInitialTenants() {
    instanceRepository.findAll().forEach(this::createAndStoreDataSource);
  }

  public DataSource getDataSource(String tenantId) {
    return dataSources.computeIfAbsent(tenantId, this::loadDataSourceForTenant);
  }

  private DataSource loadDataSourceForTenant(String tenantId) {
    InstanceEntity instance = instanceRepository.findByTenantIdIgnoreCase(tenantId)
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
    config.setPoolName("Hikari-" + instance.getTenantId());

    HikariDataSource ds = new HikariDataSource(config);
    dataSources.put(instance.getTenantId(), ds);
    return ds;
  }
}
