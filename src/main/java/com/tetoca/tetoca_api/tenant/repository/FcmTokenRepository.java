package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.tetoca.tetoca_api.tenant.model.FcmToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

  /**
   * Busca un token por su valor para evitar duplicados.
   * @param token El valor del token de Expo/FCM.
   * @return Un Optional que contiene el FcmToken si existe.
   */
  Optional<FcmToken> findByToken(String token);

  /**
   * Obtiene todos los tokens activos para un cliente específico.
   * Crucial para enviar notificaciones push a todos los dispositivos de un usuario.
   * @param clientId El ID del CompanyClient.
   * @return Una lista de strings con los tokens activos.
   */
  @Query("SELECT f.token FROM FcmToken f WHERE f.companyClient.id = :clientId AND f.recordStatus = 'A'")
  List<String> findAllActiveTokensByClientId(Integer clientId);
}
