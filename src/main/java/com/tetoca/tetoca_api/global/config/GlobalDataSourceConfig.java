package com.tetoca.tetoca_api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.tetoca.tetoca_api.global.repository",
  entityManagerFactoryRef = "globalEntityManagerFactory",
  transactionManagerRef = "globalTransactionManager"
)
public class GlobalDataSourceConfig {

  @Bean(name = "globalDataSource")
  @Primary
  public DataSource globalDataSource(
    @Value("${spring.datasource.url}") String url,
    @Value("${spring.datasource.username}") String username,
    @Value("${spring.datasource.password}") String password,
    @Value("${spring.datasource.driver-class-name}") String driver
  ) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName(driver);
    return new HikariDataSource(config);
  }

  @Primary
  @Bean(name = "globalEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean globalEntityManagerFactory(
      @Qualifier("globalDataSource") DataSource globalDataSource,
      Environment env) {

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(globalDataSource);
    factory.setPackagesToScan("com.tetoca.tetoca_api.global.model");
    factory.setPersistenceUnitName("global");

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

  @Primary
  @Bean(name = "globalTransactionManager")
  public PlatformTransactionManager globalTransactionManager(
      @Qualifier("globalEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {

    return new JpaTransactionManager(factory.getObject());
  }
}
