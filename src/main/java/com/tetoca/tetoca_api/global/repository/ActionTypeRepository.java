package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.ActionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionTypeEntity, Integer> {}
