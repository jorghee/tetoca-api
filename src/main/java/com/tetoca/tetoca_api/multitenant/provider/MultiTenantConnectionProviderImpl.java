package com.tetoca.tetoca_api.multitenant.provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

import com.tetoca.tetoca_api.global.entity.InstanceEntity;
import com.tetoca.tetoca_api.global.repository.InstanceRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

  private static final long serialVersionUID = 1L;
  private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  @Autowired
  private InstanceRepository instanceRepository;

  @Override
  public Connection getConnection(String tenantIdentifier) throws SQLException {
    return getDataSource(tenantIdentifier).getConnection();
  }

  @Override
  public DataSource selectDataSource(String tenantIdentifier) {
    return getDataSource(tenantIdentifier);
  }

  private DataSource getDataSource(String tenantIdentifier) {
    return dataSources.computeIfAbsent(tenantIdentifier, this::createDataSourceForTenant);
  }

  private DataSource createDataSourceForTenant(String tenantIdentifier) {
    InstanceEntity instance = instanceRepository.findByEmpresaNombreIgnoreCase(tenantIdentifier)
      .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantIdentifier));

    // Use confidential.properties to credentials
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(instance.getInsUriCon());
    ds.setUsername("db_user");
    ds.setPassword("db_pass");
    ds.setPoolName("Hikari-" + tenantIdentifier);
    ds.setMaximumPoolSize(5);
    return ds;
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return false;
  }

  @Override
  public Connection getAnyConnection() throws SQLException {
    throw new UnsupportedOperationException("getAnyConnection not supported");
  }

  @Override
  public void releaseAnyConnection(Connection connection) throws SQLException {
    throw new UnsupportedOperationException("releaseAnyConnection not supported");
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
