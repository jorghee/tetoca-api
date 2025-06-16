package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.QueueEvent;
import com.tetoca.tetoca_api.company.models.Queue;
import com.tetoca.tetoca_api.company.models.QueueStatus;
import com.tetoca.tetoca_api.company.models.Worker;

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
