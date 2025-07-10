package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.repository.TurnRepository;
import com.tetoca.tetoca_api.tenant.service.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para consultas públicas sobre turnos.
 * No requiere autenticación.
 */
@RestController
@RequestMapping("/api/tenant/{tenantId}/public")
@RequiredArgsConstructor
public class PublicTurnController {

  private final TurnRepository turnRepository;
  private final TurnManagementService turnManagementService;

  /**
   * Endpoint para obtener la posición actual de un turno en una cola.
   * Endpoint público que no requiere autenticación.
   *
   * @param turnId El ID del turno a consultar.
   * @return Un objeto con la información de posición.
   */
  @GetMapping("/turns/{turnId}/position")
  public ResponseEntity<Map<String, Object>> getTurnPosition(
      @PathVariable String tenantId,
      @PathVariable Long turnId) {
    
    Turn turn = turnRepository.findById(turnId)
        .orElseThrow(() -> new ResourceNotFoundException("Turn", "id", turnId));
    
    Integer queueId = turn.getQueueRegistration().getQueue().getId();
    Integer orderNumber = turn.getOrderNumber();
    
    // Calculamos cuántos turnos hay por delante si está en espera
    Integer turnsAhead = 0;
    String status = turn.getTurnStatus().getName();
    
    if ("EN_ESPERA".equals(status)) {
      turnsAhead = turnManagementService.countTurnsAhead(queueId, orderNumber);
    } else if ("PAUSADO".equals(status)) {
      turnsAhead = -1; // Indicador especial para estado pausado
    }
    
    // Estimamos el tiempo de espera
    Integer estimatedTimePerTurn = turn.getQueueRegistration().getQueue().getEstimatedTimePerTurn();
    Integer estimatedWaitTime = estimatedTimePerTurn != null ? estimatedTimePerTurn * turnsAhead : null;
    
    Map<String, Object> response = new HashMap<>();
    response.put("turnId", turn.getId());
    response.put("queueId", queueId);
    response.put("queueName", turn.getQueueRegistration().getQueue().getName());
    response.put("orderNumber", orderNumber);
    response.put("status", status);
    response.put("turnsAhead", turnsAhead);
    response.put("estimatedWaitTime", estimatedWaitTime);
    
    return ResponseEntity.ok(response);
  }
}
