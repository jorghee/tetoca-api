package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.Queue;
import com.tetoca.tetoca_api.tenant.model.QueueEvent;
import com.tetoca.tetoca_api.tenant.model.QueueStatus;
import com.tetoca.tetoca_api.tenant.model.Worker;

import java.util.List;

@Repository
public interface QueueEventRepository extends JpaRepository<QueueEvent, Long> {
    
    List<QueueEvent> findByQueue(Queue queue);
    
    List<QueueEvent> findByWorker(Worker worker);
    
    List<QueueEvent> findByNewStatus(QueueStatus newStatus);
    
    List<QueueEvent> findByQueueOrderByEventDateTimeDesc(Queue queue);
    
    List<QueueEvent> findByEventDateTimeBetween(Long startDateTime, Long endDateTime);
    
    List<QueueEvent> findByRecordStatus(String recordStatus);
}
