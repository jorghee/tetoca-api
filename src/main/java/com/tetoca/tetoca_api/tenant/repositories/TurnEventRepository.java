package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Turn;
import com.tetoca.tetoca_api.tenant.models.TurnEvent;
import com.tetoca.tetoca_api.tenant.models.TurnStatus;

import java.util.List;

@Repository
public interface TurnEventRepository extends JpaRepository<TurnEvent, Long> {
    
    List<TurnEvent> findByTurn(Turn turn);
    
    List<TurnEvent> findByNewStatus(TurnStatus newStatus);
    
    List<TurnEvent> findByTurnOrderByEventDateTimeDesc(Turn turn);
    
    List<TurnEvent> findByEventDateTimeBetween(Long startDateTime, Long endDateTime);
    
    List<TurnEvent> findByRecordStatus(String recordStatus);
}
