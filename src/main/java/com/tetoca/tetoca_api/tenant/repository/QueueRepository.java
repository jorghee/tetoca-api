package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.Agency;
import com.tetoca.tetoca_api.tenant.model.Queue;
import com.tetoca.tetoca_api.tenant.model.QueueStatus;
import com.tetoca.tetoca_api.tenant.model.QueueType;
import com.tetoca.tetoca_api.tenant.model.Turn;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Integer> {
    
    Optional<Queue> findByName(String name);
    
    List<Queue> findByQueueType(QueueType queueType);
    
    List<Queue> findByAgency(Agency agency);
    
    List<Queue> findByQueueStatus(QueueStatus queueStatus);
    
    List<Queue> findByRecordStatus(String recordStatus);
    
    // Colas activas por agencia
    @Query("SELECT q FROM Queue q WHERE q.agency.id = :agencyId AND q.recordStatus = 'A'")
    List<Queue> findActiveQueuesByAgencyId(@Param("agencyId") Integer agencyId);
    
    List<Queue> findByAgencyAndQueueStatus(Agency agency, QueueStatus queueStatus);
    
    List<Queue> findByNameContainingIgnoreCase(String name);
    
    // Contar turnos en espera en una cola
    @Query("SELECT COUNT(t) FROM Turn t WHERE t.queueRegistration.queue.id = :queueId AND " +
           "t.turnStatus.name IN ('WAITING', 'PAUSED') AND t.recordStatus = 'A'")
    Integer countWaitingTurns(@Param("queueId") Integer queueId);
    
    // Obtener turno actual siendo atendido
    @Query("SELECT t FROM Turn t WHERE t.queueRegistration.queue.id = :queueId AND " +
           "t.turnStatus.name = 'BEING_SERVED' AND t.recordStatus = 'A' " +
           "ORDER BY t.generationDateTime DESC")
    List<Turn> findCurrentServingTurn(@Param("queueId") Integer queueId);
    
    boolean existsByNameAndAgency(String name, Agency agency);
}