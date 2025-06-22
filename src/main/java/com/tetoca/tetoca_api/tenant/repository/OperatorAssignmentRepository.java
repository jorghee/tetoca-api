package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.OperatorAssignment;
import com.tetoca.tetoca_api.tenant.model.Queue;

import java.util.List;

@Repository
public interface OperatorAssignmentRepository extends JpaRepository<OperatorAssignment, Long> {
    
  /**
   * Verifica si un operario está asignado a una cola específica.
   */
  boolean existsByOperator_WorkerIdAndQueue_IdAndRecordStatus(Integer workerId, Integer queueId, String recordStatus);

  /**
   * Obtiene todas las colas asignadas a un operario.
   */
  @Query("SELECT oa.queue FROM OperatorAssignment oa WHERE oa.operator.worker.id = :workerId AND oa.recordStatus = 'A'")
  List<Queue> findAssignedQueuesByWorkerId(Integer workerId);
}
