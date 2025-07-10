package com.tetoca.tetoca_api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Permitir orígenes específicos (desarrollo local)
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:*",
        "http://127.0.0.1:*",
        "https://localhost:*",
        "https://127.0.0.1:*"));

    // Métodos HTTP permitidos
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    // Headers permitidos
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // Permitir credenciales
    configuration.setAllowCredentials(true);

    // Headers expuestos al cliente
    configuration.setExposedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With"));

    // Tiempo de cache para preflight
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
