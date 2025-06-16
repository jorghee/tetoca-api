package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Operator;
import com.tetoca.tetoca_api.tenant.models.QueueRegistration;
import com.tetoca.tetoca_api.tenant.models.Turn;
import com.tetoca.tetoca_api.tenant.models.TurnStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {
    
    List<Turn> findByQueueRegistration(QueueRegistration queueRegistration);
    
    List<Turn> findByTurnStatus(TurnStatus turnStatus);
    
    List<Turn> findByOperator(Operator operator);
    
    List<Turn> findByRecordStatus(String recordStatus);
    
    @Query("SELECT t FROM Turn t WHERE t.queueRegistration.queue.id = :queueId ORDER BY t.orderNumber")
    List<Turn> findByQueueIdOrderByOrderNumber(@Param("queueId") Integer queueId);
    
    @Query("SELECT MAX(t.orderNumber) FROM Turn t WHERE t.queueRegistration.queue.id = :queueId")
    Optional<Integer> findMaxOrderNumberByQueueId(@Param("queueId") Integer queueId);
    
    List<Turn> findByGenerationDateTimeBetween(Long startDateTime, Long endDateTime);
}
