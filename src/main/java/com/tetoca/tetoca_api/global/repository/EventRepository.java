package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {}
