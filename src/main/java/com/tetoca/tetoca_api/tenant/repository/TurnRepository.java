package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
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
   * @param recordStatus Estado del registro (ej. "A" para activo).
   * @return El número de turnos por delante.
   */
  long countByQueueRegistration_Queue_IdAndOrderNumberLessThanAndTurnStatus_NameAndRecordStatus(
      Integer queueId, Integer orderNumber, String statusName, String recordStatus);
      
  /**
   * Desplaza los números de orden para acomodar un turno que se reanuda en su posición original.
   * Incrementa en 1 el orderNumber de todos los turnos que tienen orderNumber >= targetPosition.
   *
   * @param queueId El ID de la cola.
   * @param targetPosition La posición a la que se quiere insertar el turno reanudado.
   * @param statusName El nombre del estado de los turnos a mover (típicamente "EN_ESPERA").
   * @return El número de turnos afectados.
   */
  @Modifying
  @Query(value = "UPDATE A3T_TURNO t SET t.TurNumOrd = t.TurNumOrd + 1 " +
                "FROM A3T_TURNO t " +
                "JOIN A2T_REGISTRO_COLA qr ON t.TurRegColCod = qr.RegColCod " +
                "JOIN AZZ_ESTADO_TURNO ts ON t.TurEstTurCod = ts.EstTurCod " +
                "WHERE qr.RegColColCod = :queueId " +
                "AND t.TurNumOrd >= :targetPosition " +
                "AND ts.EstTurNom = :statusName " +
                "AND t.TurEstReg = 'A'", nativeQuery = true)
  int shiftOrderNumbersToAccommodate(
      @Param("queueId") Integer queueId,
      @Param("targetPosition") Integer targetPosition,
      @Param("statusName") String statusName);
}
