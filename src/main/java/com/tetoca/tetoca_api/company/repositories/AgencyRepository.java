package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.Agency;
import com.tetoca.tetoca_api.company.models.Division;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Integer> {
    
    Optional<Agency> findByName(String name);
    
    List<Agency> findByDivision(Division division);
    
    List<Agency> findByRecordStatus(String recordStatus);
    
    List<Agency> findByDivisionAndRecordStatus(Division division, String recordStatus);
    
    List<Agency> findByNameContainingIgnoreCase(String name);
    
    boolean existsByNameAndDivision(String name, Division division);
}
