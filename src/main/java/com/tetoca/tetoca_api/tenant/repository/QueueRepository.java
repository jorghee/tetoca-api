package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.tetoca.tetoca_api.tenant.model.Queue;

import java.util.List;

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
}
