package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.global.model.ActionTypeEntity;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionTypeEntity, Integer> {}
