package com.tetoca.tetoca_api.multitenant.provider;

import com.tetoca.tetoca_api.global.entity.InstanceEntity;
import com.tetoca.tetoca_api.global.repository.InstanceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

  private final InstanceRepository instanceRepository;
  private final DataSource defaultDataSource;

  private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();

  @Override
  public Connection getConnection(String tenantIdentifier) throws SQLException {
    return getDataSource(tenantIdentifier).getConnection();
  }

  @Override
  public Connection getConnection() throws SQLException {
    return defaultDataSource.getConnection();
  }

  @Override
  public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
    connection.close();
  }

  @Override
  public void releaseConnection(Connection connection) throws SQLException {
    connection.close();
  }

  private DataSource getDataSource(String tenantIdentifier) {
    return tenantDataSources.computeIfAbsent(tenantIdentifier, this::createDataSourceForTenant);
  }

  private DataSource createDataSourceForTenant(String tenantIdentifier) {
    InstanceEntity instance = instanceRepository.findByTenantIdIgnoreCase(tenantIdentifier)
      .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantIdentifier));

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(instance.getDbUri());
    config.setUsername(instance.getDbUser());
    config.setPassword(instance.getDbPassword());
    config.setMaximumPoolSize(5);
    config.setPoolName("Hikari-" + tenantIdentifier);

    return new HikariDataSource(config);
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return false;
  }

  @Override
  public boolean isUnwrappableAs(Class<?> unwrapType) {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> unwrapType) {
    return null;
  }
}
