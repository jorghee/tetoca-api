package com.tetoca.tetoca_api.repositories.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.models.company.Operator;
import com.tetoca.tetoca_api.models.company.Agency;
import com.tetoca.tetoca_api.models.company.Worker;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    
    Optional<Operator> findByWorker(Worker worker);
    
    List<Operator> findByAgency(Agency agency);
    
    List<Operator> findByRecordStatus(String recordStatus);
    
    List<Operator> findByAgencyAndRecordStatus(Agency agency, String recordStatus);
}
