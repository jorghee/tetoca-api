package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.security.ClientDetailsImpl;
import com.tetoca.tetoca_api.tenant.dto.request.JoinQueueRequest;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.service.QueueJoiningService;
import com.tetoca.tetoca_api.tenant.service.TurnManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tenant/{tenantId}/queues")
@RequiredArgsConstructor
public class ClientQueueController {

  private final QueueJoiningService queueJoiningService;
  private final TurnManagementService turnManagementService;

  /**
   * Endpoint para que un cliente se una a una cola específica.
   * La autenticación del cliente (obtenida del token JWT global) es requerida.
   *
   * @param tenantId El ID del tenant (empresa) a la que pertenece la cola.
   * @param queueId El ID de la cola a la que se desea unir.
   * @param request El cuerpo de la solicitud, que contiene el pushToken.
   * @param authentication El objeto de autenticación de Spring Security, inyectado automáticamente.
   * @return Una ResponseEntity con el Turn creado y un estado HTTP 201 (Created).
   */
  @PostMapping("/{queueId}/join")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_CLIENT')")
  public ResponseEntity<Turn> joinQueue(
      @PathVariable String tenantId,
      @PathVariable Integer queueId,
      @Valid @RequestBody JoinQueueRequest request,
      Authentication authentication) {
    
    ClientDetailsImpl clientDetails = (ClientDetailsImpl) authentication.getPrincipal();

    Turn createdTurn = queueJoiningService.joinQueue(
        queueId,
        clientDetails.getExternalUid(),
        clientDetails.getFullName(),
        request.getPushToken()
    );

    return new ResponseEntity<>(createdTurn, HttpStatus.CREATED);
  }
  
  /**
   * Endpoint para pausar un turno específico.
   * El cliente debe ser el propietario del turno.
   *
   * @param tenantId El ID del tenant.
   * @param turnId El ID del turno a pausar.
   * @param authentication El objeto de autenticación.
   * @return El turno pausado.
   */
  @PostMapping("/turns/{turnId}/pause")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_CLIENT')")
  public ResponseEntity<Turn> pauseTurn(
      @PathVariable String tenantId,
      @PathVariable Long turnId,
      Authentication authentication) {
      
    ClientDetailsImpl clientDetails = (ClientDetailsImpl) authentication.getPrincipal();
    
    Turn pausedTurn = turnManagementService.pauseTurn(
        turnId,
        clientDetails.getExternalUid()
    );
    
    return ResponseEntity.ok(pausedTurn);
  }
  
  /**
   * Endpoint para reanudar un turno que estaba pausado.
   * El cliente debe ser el propietario del turno.
   *
   * @param tenantId El ID del tenant.
   * @param turnId El ID del turno a reanudar.
   * @param authentication El objeto de autenticación.
   * @return El turno reactivado en su posición original.
   */
  @PostMapping("/turns/{turnId}/resume")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_CLIENT')")
  public ResponseEntity<Turn> resumeTurn(
      @PathVariable String tenantId,
      @PathVariable Long turnId,
      Authentication authentication) {
      
    ClientDetailsImpl clientDetails = (ClientDetailsImpl) authentication.getPrincipal();
    
    Turn resumedTurn = turnManagementService.resumeTurn(
        turnId,
        clientDetails.getExternalUid()
    );
    
    return ResponseEntity.ok(resumedTurn);
  }
  
  /**
   * Endpoint para que un cliente cancele su turno.
   *
   * @param tenantId El ID del tenant.
   * @param turnId El ID del turno a cancelar.
   * @param authentication El objeto de autenticación.
   * @return Respuesta vacía con código 204 (No Content).
   */
  @DeleteMapping("/turns/{turnId}")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_CLIENT')")
  public ResponseEntity<Void> cancelTurn(
      @PathVariable String tenantId,
      @PathVariable Long turnId,
      Authentication authentication) {
      
    ClientDetailsImpl clientDetails = (ClientDetailsImpl) authentication.getPrincipal();
    
    turnManagementService.cancelTurn(turnId, clientDetails.getExternalUid());
    
    return ResponseEntity.noContent().build();
  }
  
  /**
   * Endpoint para que un cliente consulte el estado de su turno.
   *
   * @param tenantId El ID del tenant.
   * @param turnId El ID del turno a consultar.
   * @param authentication El objeto de autenticación.
   * @return El turno con su estado actual.
   */
  @GetMapping("/turns/{turnId}")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_CLIENT')")
  public ResponseEntity<Turn> getTurnStatus(
      @PathVariable String tenantId,
      @PathVariable Long turnId,
      Authentication authentication) {
      
    ClientDetailsImpl clientDetails = (ClientDetailsImpl) authentication.getPrincipal();
    
    Turn turn = turnManagementService.getTurnAndValidateClient(turnId, clientDetails.getExternalUid());
    
    return ResponseEntity.ok(turn);
  }
  
  /**
   * Endpoint para que un cliente consulte la posición de su turno en la cola.
   */
  @GetMapping("/turns/{turnId}/position")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_CLIENT')")
  public ResponseEntity<Map<String, Object>> getTurnPosition(
      @PathVariable String tenantId,
      @PathVariable Long turnId,
      Authentication authentication) {
      
    ClientDetailsImpl clientDetails = (ClientDetailsImpl) authentication.getPrincipal();
    
    // Obtenemos el turno usando el servicio
    Turn turn = turnManagementService.getTurnAndValidateClient(turnId, clientDetails.getExternalUid());
    
    Integer queueId = turn.getQueueRegistration().getQueue().getId();
    Integer orderNumber = turn.getOrderNumber();
    
    // Calculamos cuántos turnos hay por delante si está en espera
    Integer turnsAhead = 0;
    String status = turn.getTurnStatus().getName();
    
    // Usamos el servicio de gestión de turnos en lugar de acceder directamente al repositorio
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
