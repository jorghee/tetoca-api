package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.model.CompanyClient;
import com.tetoca.tetoca_api.tenant.model.FcmToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    
    List<FcmToken> findByCompanyClient(CompanyClient client);
    
    Optional<FcmToken> findByToken(String token);
    
    List<FcmToken> findByRecordStatus(String recordStatus);
    
    List<FcmToken> findByCompanyClientAndRecordStatus(CompanyClient client, String recordStatus);
    
    boolean existsByToken(String token);
    
    void deleteByToken(String token);
}
