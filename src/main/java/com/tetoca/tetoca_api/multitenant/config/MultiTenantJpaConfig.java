package com.tetoca.tetoca_api.multitenant.config;

import com.tetoca.tetoca_api.multitenant.provider.MultiTenantConnectionProviderImpl;
import com.tetoca.tetoca_api.multitenant.resolver.TenantIdentifierResolver;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment as SpringEnvironment;
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
public class MultiTenantJpaConfig {

  @Autowired
  private MultiTenantConnectionProviderImpl connectionProvider;

  @Autowired
  private TenantIdentifierResolver tenantResolver;

  @Autowired
  private DataSource defaultDataSource;

  @Autowired
  private SpringEnvironment env;

  @Bean(name = "tenantEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(EntityManagerFactoryBuilder builder) {
    Map<String, Object> props = new HashMap<>();

    props.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
    props.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
    props.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);

    props.put(Environment.DIALECT, env.getProperty("spring.jpa.properties.hibernate.dialect"));
    props.put(Environment.SHOW_SQL, env.getProperty("spring.jpa.show-sql"));
    props.put(Environment.FORMAT_SQL, env.getProperty("spring.jpa.properties.hibernate.format_sql"));
    props.put(Environment.HBM2DDL_AUTO, env.getProperty("spring.jpa.hibernate.ddl-auto"));

    return builder
      .dataSource(defaultDataSource)
      .packages("com.tetoca.tetoca_api.tenant.entity")
      .persistenceUnit("tenant")
      .properties(props)
      .build();
  }

  @Bean(name = "tenantTransactionManager")
  public PlatformTransactionManager tenantTransactionManager(EntityManagerFactory tenantEntityManagerFactory) {
    return new JpaTransactionManager(tenantEntityManagerFactory);
  }
}
