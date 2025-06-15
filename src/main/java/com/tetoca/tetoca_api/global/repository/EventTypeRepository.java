package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.global.models.EventTypeEntity;

@Repository
public interface EventTypeRepository extends JpaRepository<EventTypeEntity, Integer> {}
