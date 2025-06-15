package com.tetoca.tetoca_api.repositories.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.models.company.Agency;
import com.tetoca.tetoca_api.models.company.Division;

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
