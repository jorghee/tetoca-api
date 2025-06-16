package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.QueueType;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueTypeRepository extends JpaRepository<QueueType, Integer> {
    
    Optional<QueueType> findByName(String name);
    
    List<QueueType> findByRecordStatus(String recordStatus);
    
    boolean existsByName(String name);
}
