package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.TurnStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnStatusRepository extends JpaRepository<TurnStatus, Integer> {
    
    Optional<TurnStatus> findByName(String name);
    
    List<TurnStatus> findByRecordStatus(String recordStatus);
    
    boolean existsByName(String name);
}
