package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Division;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Integer> {
    
    Optional<Division> findByName(String name);
    
    List<Division> findByRecordStatus(String recordStatus);
    
    List<Division> findByNameContainingIgnoreCase(String name);
    
    boolean existsByName(String name);
}
