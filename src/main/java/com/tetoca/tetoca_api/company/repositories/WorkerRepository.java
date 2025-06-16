package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.Worker;
import com.tetoca.tetoca_api.company.models.WorkerType;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    
    Optional<Worker> findByEmail(String email);
    
    List<Worker> findByWorkerType(WorkerType workerType);
    
    List<Worker> findByRecordStatus(String recordStatus);
    
    boolean existsByEmail(String email);
    
    List<Worker> findByFullNameContainingIgnoreCase(String name);
}
