package com.tetoca.tetoca_api.multitenant.config;

import com.tetoca.tetoca_api.multitenant.datasource.TenantDataSourceProvider;
import com.tetoca.tetoca_api.multitenant.datasource.TenantRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.tetoca.tetoca_api.tenant.repository",
  entityManagerFactoryRef = "tenantEntityManagerFactory",
  transactionManagerRef = "tenantTransactionManager"
)
public class TenantDataSourceConfig {

  @Autowired
  private TenantDataSourceProvider tenantDataSourceProvider;

  @Autowired
  private Environment env;

  @Bean
  public DataSource tenantDataSource() {
    return new TenantRoutingDataSource(tenantDataSourceProvider);
  }

  @Bean(name = "tenantEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(EntityManagerFactoryBuilder builder) {
    Map<String, Object> jpaProperties = new HashMap<>();
    jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
    jpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
    jpaProperties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
    jpaProperties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));

    return builder
      .dataSource(tenantDataSource())
      .packages("com.tetoca.tetoca_api.tenant.entity")
      .persistenceUnit("tenant")
      .properties(jpaProperties)
      .build();
  }

  @Bean(name = "tenantTransactionManager")
  public PlatformTransactionManager tenantTransactionManager(LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory) {
    return new JpaTransactionManager(tenantEntityManagerFactory.getObject());
  }
}
