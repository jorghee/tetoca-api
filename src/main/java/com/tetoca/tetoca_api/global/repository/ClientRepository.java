package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.global.model.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

  /**
   * Busca un cliente por su UID externo, que es el identificador único
   * proporcionado por el proveedor de OAuth (e.g., Google, Facebook).
   *
   * @param externalUid El identificador único del proveedor.
   * @return Un Optional que contiene el Client si se encuentra.
   */
  Optional<Client> findByExternalUid(String externalUid);
}
