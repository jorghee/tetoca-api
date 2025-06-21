package com.tetoca.tetoca_api.multitenant.config;

import com.tetoca.tetoca_api.multitenant.datasource.TenantDataSourceProvider;
import com.tetoca.tetoca_api.multitenant.datasource.TenantRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.tetoca.tetoca_api.tenant.repository",
  entityManagerFactoryRef = "tenantEntityManagerFactory",
  transactionManagerRef = "tenantTransactionManager"
)
public class TenantDataSourceConfig {

  @Bean
  public TenantDataSourceProvider tenantDataSourceProvider(
      ApplicationContext applicationContext,
      @Qualifier("globalDataSource") DataSource globalDataSource) {
    return new TenantDataSourceProvider(applicationContext, globalDataSource);
  }

  @Bean(name = "tenantDataSource")
  @DependsOn("tenantDataSourceProvider")
  public DataSource tenantDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
    return new TenantRoutingDataSource(tenantDataSourceProvider);
  }

  @Bean(name = "tenantEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
      @Qualifier("tenantDataSource") DataSource dataSource,
      Environment env) {

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(dataSource);
    factory.setPackagesToScan("com.tetoca.tetoca_api.tenant.model");
    factory.setPersistenceUnitName("tenant");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    factory.setJpaVendorAdapter(vendorAdapter);

    Properties jpaProperties = new Properties();
    jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
    jpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
    jpaProperties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
    jpaProperties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
    factory.setJpaProperties(jpaProperties);

    return factory;
  }

  @Bean(name = "tenantTransactionManager")
  public PlatformTransactionManager tenantTransactionManager(
      @Qualifier("tenantEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {

    return new JpaTransactionManager(factory.getObject());
  }
}
