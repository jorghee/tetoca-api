package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.Agency;
import com.tetoca.tetoca_api.tenant.model.AgencyAdmin;
import com.tetoca.tetoca_api.tenant.model.Worker;

import java.util.List;

@Repository
public interface AgencyAdminRepository extends JpaRepository<AgencyAdmin, Integer> {
    
    List<AgencyAdmin> findByAgency(Agency agency);
    
    List<AgencyAdmin> findByWorker(Worker worker);
    
    List<AgencyAdmin> findByRecordStatus(String recordStatus);
    
    List<AgencyAdmin> findByAgencyAndRecordStatus(Agency agency, String recordStatus);
    
    boolean existsByAgencyAndWorker(Agency agency, Worker worker);
}
