package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.EventTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTypeRepository extends JpaRepository<EventTypeEntity, Integer> {}
