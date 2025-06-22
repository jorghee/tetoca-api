package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.model.SaaSAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaaSAdminRepository extends JpaRepository<SaaSAdmin, Integer> {

  /**
   * Busca a un administrador de SaaS por su dirección de correo electrónico.
   * Este método es fundamental para el proceso de login.
   *
   * @param email La dirección de correo electrónico a buscar.
   * @return Un Optional que contiene el SaaSAdmin si se encuentra.
   */
  Optional<SaaSAdmin> findByEmail(String email);
}
