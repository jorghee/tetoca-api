package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tetoca.tetoca_api.tenant.model.Queue;
import com.tetoca.tetoca_api.tenant.model.Turn;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Integer> {

  /**
   * Busca todas las colas activas pertenecientes a una agencia específica.
   * @param agencyId El ID de la agencia.
   * @return Una lista de colas.
   */
  List<Queue> findAllByAgency_IdAndRecordStatus(Integer agencyId, String recordStatus);

  /**
   * Calcula el siguiente número de orden para un nuevo turno en una cola.
   * Utiliza COALESCE para devolver 0 si la cola está vacía.
   * @param queueId El ID de la cola.
   * @return El número de orden máximo actual en la cola.
   */
  @Query("SELECT COALESCE(MAX(t.orderNumber), 0) FROM Turn t WHERE t.queueRegistration.queue.id = :queueId")
  Integer findMaxOrderNumberByQueueId(Integer queueId);

  /**
   * Cuenta el número de turnos en estado 'EN_ESPERA' para una cola.
   * Útil para el DTO QueueResponse.
   *
   * @param queueId El ID de la cola.
   * @return El número de personas en espera.
   */
  @Query("SELECT COUNT(t) FROM Turn t " +
         "WHERE t.queueRegistration.queue.id = :queueId " +
         "AND t.turnStatus.name = 'EN_ESPERA' " +
         "AND t.recordStatus = 'A'")
  Integer countWaitingTurns(@Param("queueId") Integer queueId);

  /**
   * Encuentra el turno que está siendo atendido actualmente (en estado 'LLAMANDO').
   * Útil para mostrar el "ticket actual" en las vistas.
   *
   * @param queueId El ID de la cola.
   * @return Una lista que contiene el turno actual (o vacía si no hay ninguno).
   */
  @Query(value = "SELECT t.* FROM A3T_TURNO t " +
         "JOIN A2T_REGISTRO_COLA qr ON t.TurRegColCod = qr.RegColCod " +
         "JOIN AZZ_ESTADO_TURNO ts ON t.TurEstTurCod = ts.EstTurCod " +
         "WHERE qr.RegColColCod = :queueId AND ts.EstTurNom = 'LLAMANDO' AND t.TurEstReg = 'A' " +
         "ORDER BY t.TurFecHorAte DESC", nativeQuery = true)
  List<Turn> findCurrentServingTurn(@Param("queueId") Integer queueId);

  /**
   * Busca una cola por su ID y carga todas sus relaciones importantes.
   * @param id El ID de la cola.
   * @return Una Queue con sus relaciones cargadas.
   */
  @Query("SELECT q FROM Queue q " +
         "LEFT JOIN FETCH q.queueStatus " +
         "LEFT JOIN FETCH q.queueType " +
         "LEFT JOIN FETCH q.agency " +
         "WHERE q.id = :id")
  Optional<Queue> findByIdWithRelationships(@Param("id") Integer id);
}
