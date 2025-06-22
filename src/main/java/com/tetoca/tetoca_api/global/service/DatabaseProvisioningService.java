package com.tetoca.tetoca_api.global.service;

import com.tetoca.tetoca_api.global.event.CompanyRegistrationEvent;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseProvisioningService {

  @Value("${spring.datasource.url}")
  private String globalDbUrl;
  @Value("${spring.datasource.username}")
  private String dbAdminUser;
  @Value("${spring.datasource.password}")
  private String dbAdminPassword;
  
  @Value("${spring.jpa.properties.hibernate.dialect}")
  private String hibernateDialect;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCompanyRegistration(CompanyRegistrationEvent event) {
    log.info("Received company registration event for tenant: {}. Starting provisioning.", event.getTenantId());
    try {
      createDatabase(event.getDbName());
      createSchemaForTenant(event.getDbUri(), event.getDbUser(), event.getDbPassword());
      log.info("Database and schema provisioned successfully for tenant: {}", event.getTenantId());
    } catch (Exception e) {
      log.error("Failed to provision database for tenant: {}", event.getTenantId(), e);
    }
  }
  
  private void createDatabase(String dbName) throws Exception {
    if (!dbName.matches("^[a-zA-Z0-9_]+$")) {
      throw new IllegalArgumentException("Invalid database name format.");
    }
    
    String maintenanceDbUrl = getMaintenanceDbUrl(globalDbUrl);

    try (Connection connection = DriverManager.getConnection(maintenanceDbUrl, dbAdminUser, dbAdminPassword);
      Statement statement = connection.createStatement()) {

      log.info("Checking if database '{}' exists...", dbName);

      String checkDbSql = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
      if (!statement.executeQuery(checkDbSql).next()) {
          log.info("Database '{}' does not exist. Creating now...", dbName);
          statement.executeUpdate("CREATE DATABASE " + dbName);
          log.info("Database '{}' created successfully.", dbName);
      } else {
          log.warn("Database '{}' already exists. Skipping creation.", dbName);
      }
    }
  }

  private void createSchemaForTenant(String dbUri, String dbUser, String dbPassword) {
    log.info("Creating schema for new tenant database: {}", dbUri);

    // Create a temporal DataSource that pointers to the new database
    HikariDataSource tenantDataSource = new HikariDataSource();
    tenantDataSource.setJdbcUrl(dbUri);
    tenantDataSource.setUsername(dbUser);
    tenantDataSource.setPassword(dbPassword);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(tenantDataSource);
    factory.setPackagesToScan("com.tetoca.tetoca_api.tenant.model");
    factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    Properties jpaProperties = new Properties();
    jpaProperties.put("hibernate.hbm2ddl.auto", "create");
    jpaProperties.put("hibernate.dialect", hibernateDialect);
    jpaProperties.put("hibernate.show_sql", "true");
    factory.setJpaProperties(jpaProperties);
    
    try {
      factory.afterPropertiesSet();
      log.info("Schema created successfully.");
    } finally {
      factory.destroy();
      tenantDataSource.close();
    }
  }
  
  private String getMaintenanceDbUrl(String originalUrl) {
    // jdbc:postgresql://localhost:5432/BD_TETOCA_GLOBAL -> jdbc:postgresql://localhost:5432/postgres
    return originalUrl.substring(0, originalUrl.lastIndexOf("/") + 1) + "postgres";
  }
}
