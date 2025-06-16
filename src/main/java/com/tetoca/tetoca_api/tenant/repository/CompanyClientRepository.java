package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.CompanyClient;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyClientRepository extends JpaRepository<CompanyClient, Integer> {
    
    Optional<CompanyClient> findByEmail(String email);
    
    Optional<CompanyClient> findByExternalUid(String externalUid);
    
    List<CompanyClient> findByRecordStatus(String recordStatus);
    
    List<CompanyClient> findByFullNameContainingIgnoreCase(String name);
    
    boolean existsByEmail(String email);
    
    boolean existsByExternalUid(String externalUid);
}
