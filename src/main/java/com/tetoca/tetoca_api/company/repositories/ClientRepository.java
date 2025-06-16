package com.tetoca.tetoca_api.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.company.models.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    
    Optional<Client> findByEmail(String email);
    
    Optional<Client> findByExternalUid(String externalUid);
    
    List<Client> findByRecordStatus(String recordStatus);
    
    List<Client> findByFullNameContainingIgnoreCase(String name);
    
    boolean existsByEmail(String email);
    
    boolean existsByExternalUid(String externalUid);
}
