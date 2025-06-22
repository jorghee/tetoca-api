package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.CompanyClient;

import java.util.Optional;

@Repository
public interface CompanyClientRepository extends JpaRepository<CompanyClient, Integer> {

  /**
   * Busca un cliente de la empresa por su UID externo.
   * Esencial para vincular al cliente global con su registro en el tenant.
   * @param externalUid El UID del cliente en la base de datos global.
   * @return Un Optional que contiene el CompanyClient si se encuentra.
   */
  Optional<CompanyClient> findByExternalUid(String externalUid);
}
