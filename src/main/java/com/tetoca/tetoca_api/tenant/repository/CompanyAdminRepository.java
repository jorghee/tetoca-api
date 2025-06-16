package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.CompanyAdmin;
import com.tetoca.tetoca_api.tenant.model.Worker;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAdminRepository extends JpaRepository<CompanyAdmin, Integer> {
    
    Optional<CompanyAdmin> findByWorker(Worker worker);
    
    List<CompanyAdmin> findByRecordStatus(String recordStatus);
    
    boolean existsByWorker(Worker worker);
}
