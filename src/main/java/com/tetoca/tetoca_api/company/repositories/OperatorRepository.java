package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.Operator;
import com.tetoca.tetoca_api.company.models.Agency;
import com.tetoca.tetoca_api.company.models.Worker;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    
    Optional<Operator> findByWorker(Worker worker);
    
    List<Operator> findByAgency(Agency agency);
    
    List<Operator> findByRecordStatus(String recordStatus);
    
    List<Operator> findByAgencyAndRecordStatus(Agency agency, String recordStatus);
}
