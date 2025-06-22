package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.QueueStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueStatusRepository extends JpaRepository<QueueStatus, Integer> {
  
  Optional<QueueStatus> findByName(String name);
  
  List<QueueStatus> findByRecordStatus(String recordStatus);
  
  boolean existsByName(String name);
}
