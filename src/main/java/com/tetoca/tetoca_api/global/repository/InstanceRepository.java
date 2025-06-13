package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {
  Optional<InstanceEntity> findByDbNameIgnoreCase(String dbName);
  Optional<InstanceEntity> findByCompanyId(Long companyId);
}
