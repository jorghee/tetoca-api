package com.tetoca.tetoca_api.repositories.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.models.company.QueueEvent;
import com.tetoca.tetoca_api.models.company.Queue;
import com.tetoca.tetoca_api.models.company.QueueStatus;
import com.tetoca.tetoca_api.models.company.Worker;

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
