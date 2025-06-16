package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.WorkerType;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerTypeRepository extends JpaRepository<WorkerType, Integer> {
    
    Optional<WorkerType> findByName(String name);
    
    List<WorkerType> findByRecordStatus(String recordStatus);
    
    boolean existsByName(String name);
}
