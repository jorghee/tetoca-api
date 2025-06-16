package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Agency;
import com.tetoca.tetoca_api.tenant.models.Operator;
import com.tetoca.tetoca_api.tenant.models.Worker;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    
    Optional<Operator> findByWorker(Worker worker);
    
    List<Operator> findByAgency(Agency agency);
    
    List<Operator> findByRecordStatus(String recordStatus);
    
    List<Operator> findByAgencyAndRecordStatus(Agency agency, String recordStatus);
}
