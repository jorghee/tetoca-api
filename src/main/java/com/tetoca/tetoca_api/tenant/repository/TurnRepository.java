package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.Operator;
import com.tetoca.tetoca_api.tenant.model.QueueRegistration;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.model.TurnStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {
    
    List<Turn> findByQueueRegistration(QueueRegistration queueRegistration);
    
    List<Turn> findByTurnStatus(TurnStatus turnStatus);
    
    List<Turn> findByOperator(Operator operator);
    
    List<Turn> findByRecordStatus(String recordStatus);
    
    // Buscar por número de orden (que actúa como código)
    @Query("SELECT t FROM Turn t WHERE t.orderNumber = :orderNumber AND " +
           "t.queueRegistration.queue.id = :queueId AND t.recordStatus = 'A'")
    Optional<Turn> findByOrderNumberAndQueueIdAndActive(@Param("orderNumber") Integer orderNumber, 
                                                       @Param("queueId") Integer queueId);
    
    // Generar código de turno basado en orden
    @Query("SELECT CONCAT(q.name, '-', LPAD(CAST(t.orderNumber AS string), 3, '0')) " +
           "FROM Turn t JOIN t.queueRegistration.queue q WHERE t.id = :turnId")
    Optional<String> generateTurnCode(@Param("turnId") Long turnId);
    
    @Query("SELECT t FROM Turn t WHERE t.queueRegistration.queue.id = :queueId ORDER BY t.orderNumber")
    List<Turn> findByQueueIdOrderByOrderNumber(@Param("queueId") Integer queueId);
    
    @Query("SELECT COALESCE(MAX(t.orderNumber), 0) FROM Turn t WHERE t.queueRegistration.queue.id = :queueId")
    Integer findMaxOrderNumberByQueueId(@Param("queueId") Integer queueId);
    
    // Contar turnos adelante en la cola
    @Query("SELECT COUNT(t) FROM Turn t WHERE t.queueRegistration.queue.id = :queueId AND " +
           "t.turnStatus.name IN ('WAITING', 'PAUSED') AND t.orderNumber < :orderNumber AND t.recordStatus = 'A'")
    Integer countTurnsAheadInQueue(@Param("queueId") Integer queueId, @Param("orderNumber") Integer orderNumber);
    
    // Turnos activos de un cliente
    @Query("SELECT t FROM Turn t WHERE t.queueRegistration.companyClient.id = :clientId AND " +
           "t.turnStatus.name IN ('WAITING', 'PAUSED') AND t.recordStatus = 'A'")
    List<Turn> findActiveTurnsForClient(@Param("clientId") Integer clientId);
    
    // Turnos en espera por cola
    @Query("SELECT t FROM Turn t WHERE t.queueRegistration.queue.id = :queueId AND " +
           "t.turnStatus.name = 'WAITING' AND t.recordStatus = 'A' ORDER BY t.orderNumber")
    List<Turn> findWaitingTurnsByQueue(@Param("queueId") Integer queueId);
    
    List<Turn> findByGenerationDateTimeBetween(Long startDateTime, Long endDateTime);
}
