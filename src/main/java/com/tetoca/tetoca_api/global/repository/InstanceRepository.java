package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {

  InstanceEntity findByDbName(String dbName);
}
