package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.model.*;
import com.tetoca.tetoca_api.tenant.repository.TurnEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TurnEventService {
    
  private final TurnEventRepository turnEventRepository;
  
  public void logTurnEvent(Turn turn, TurnStatus previousStatus, TurnStatus newStatus, String reason) {
    TurnEvent event = new TurnEvent();
    event.setTurn(turn);
    event.setPreviousStatus(previousStatus);
    event.setNewStatus(newStatus);
    event.setReason(reason);
    event.setEventDateTime(System.currentTimeMillis());
    turnEventRepository.save(event);
  }
}
