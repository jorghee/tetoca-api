package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Integer> {}
