package com.tetoca.tetoca_api.repositories.global;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.models.global.EventEntity;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {}
