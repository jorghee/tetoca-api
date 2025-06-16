package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.global.models.AuditEntity;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Integer> {}
