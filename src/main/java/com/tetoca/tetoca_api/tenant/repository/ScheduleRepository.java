package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.Queue;
import com.tetoca.tetoca_api.tenant.model.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    
    List<Schedule> findByQueue(Queue queue);
    
    List<Schedule> findByDayOfWeek(Integer dayOfWeek);
    
    List<Schedule> findByQueueAndDayOfWeek(Queue queue, Integer dayOfWeek);
    
    List<Schedule> findByRecordStatus(String recordStatus);
    
    List<Schedule> findByQueueIdAndRecordStatus(Integer queueId, String recordStatus);
}
