package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.DivisionAdmin;
import com.tetoca.tetoca_api.company.models.Division;
import com.tetoca.tetoca_api.company.models.Worker;

import java.util.List;

@Repository
public interface DivisionAdminRepository extends JpaRepository<DivisionAdmin, Integer> {
    
    List<DivisionAdmin> findByDivision(Division division);
    
    List<DivisionAdmin> findByWorker(Worker worker);
    
    List<DivisionAdmin> findByRecordStatus(String recordStatus);
    
    List<DivisionAdmin> findByDivisionAndRecordStatus(Division division, String recordStatus);
    
    boolean existsByDivisionAndWorker(Division division, Worker worker);
}
