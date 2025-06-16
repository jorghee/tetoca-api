package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.Schedule;
import com.tetoca.tetoca_api.company.models.Queue;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    
    List<Schedule> findByQueue(Queue queue);
    
    List<Schedule> findByDayOfWeek(Integer dayOfWeek);
    
    List<Schedule> findByQueueAndDayOfWeek(Queue queue, Integer dayOfWeek);
    
    List<Schedule> findByRecordStatus(String recordStatus);
    
    List<Schedule> findByQueueIdAndRecordStatus(Integer queueId, String recordStatus);
}
