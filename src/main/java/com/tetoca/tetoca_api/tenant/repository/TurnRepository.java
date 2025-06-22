package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.tetoca.tetoca_api.tenant.model.Turn;

import java.util.Optional;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {
  /**
   * Busca el siguiente turno en una cola que está en un estado específico (ej. "EN_ESPERA").
   * Ordena por el número de orden para garantizar el sistema FIFO (First-In, First-Out).
   * @param queueId El ID de la cola.
   * @param statusName El nombre del estado del turno (ej. "EN_ESPERA").
   * @return Un Optional que contiene el siguiente Turn si existe.
   */
  @Query(value = "SELECT t.* FROM A3T_TURNO t " +
                 "JOIN A2T_REGISTRO_COLA qr ON t.TurRegColCod = qr.RegColCod " +
                 "JOIN AZZ_ESTADO_TURNO ts ON t.TurEstTurCod = ts.EstTurCod " +
                 "WHERE qr.RegColColCod = :queueId AND ts.EstTurNom = :statusName AND t.TurEstReg = 'A' " +
                 "ORDER BY t.TurNumOrd ASC LIMIT 1", nativeQuery = true)
  Optional<Turn> findNextTurnInQueue(Integer queueId, String statusName);

  /**
   * Cuenta cuántos turnos hay delante de un turno específico en la misma cola.
   * @param queueId El ID de la cola.
   * @param orderNumber El número de orden del turno actual.
   * @param statusName El estado de los turnos a contar (ej. "EN_ESPERA").
   * @return El número de turnos por delante.
   */
  long countByQueueRegistration_Queue_IdAndOrderNumberLessThanAndTurnStatus_Name(
      Integer queueId, Integer orderNumber, String statusName, String recordStatus);
}
