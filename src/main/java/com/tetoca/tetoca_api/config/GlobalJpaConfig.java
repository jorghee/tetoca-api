package com.tetoca.tetoca_api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.tetoca.tetoca_api.global.repository",
  entityManagerFactoryRef = "globalEntityManagerFactory",
  transactionManagerRef = "globalTransactionManager"
)
public class GlobalJpaConfig {

  @Primary
  @Bean(name = "globalEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean globalEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("dataSource") DataSource dataSource) {

    return builder
      .dataSource(dataSource)
      .packages("com.tetoca.tetoca_api.global.entity")
      .persistenceUnit("global")
      .build();
  }

  @Primary
  @Bean(name = "globalTransactionManager")
  public PlatformTransactionManager globalTransactionManager(
      @Qualifier("globalEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {

    return new JpaTransactionManager(factory.getObject());
  }
}
