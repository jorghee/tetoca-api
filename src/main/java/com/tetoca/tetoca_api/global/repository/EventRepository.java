package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.global.model.EventEntity;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {}
