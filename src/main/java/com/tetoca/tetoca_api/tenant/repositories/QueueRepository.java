package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Agency;
import com.tetoca.tetoca_api.tenant.models.Queue;
import com.tetoca.tetoca_api.tenant.models.QueueStatus;
import com.tetoca.tetoca_api.tenant.models.QueueType;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Integer> {
    
    Optional<Queue> findByName(String name);
    
    List<Queue> findByQueueType(QueueType queueType);
    
    List<Queue> findByAgency(Agency agency);
    
    List<Queue> findByQueueStatus(QueueStatus queueStatus);
    
    List<Queue> findByRecordStatus(String recordStatus);
    
    List<Queue> findByAgencyAndQueueStatus(Agency agency, QueueStatus queueStatus);
    
    List<Queue> findByNameContainingIgnoreCase(String name);
    
    boolean existsByNameAndAgency(String name, Agency agency);
}
