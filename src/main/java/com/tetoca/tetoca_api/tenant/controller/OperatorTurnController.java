package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.security.UserDetailsImpl;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.service.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants/{tenantId}")
@RequiredArgsConstructor
public class OperatorTurnController {

  private final TurnManagementService turnManagementService;

  /**
   * Endpoint para que un operario llame al siguiente turno en una cola.
   * Requiere rol de OPERATOR y que el operario esté asignado a esa cola.
   *
   * @param queueId El ID de la cola desde la cual llamar al siguiente turno.
   * @param authentication El objeto de autenticación del operario.
   * @return Una ResponseEntity con el Turno que fue llamado.
   */
  @PostMapping("/operator/queues/{queueId}/call-next")
  @PreAuthorize("hasRole('ROLE_OPERATOR') and @securityService.isOperatorAssignedToQueue(authentication, #queueId)")
  public ResponseEntity<Turn> callNextTurn(
      @PathVariable Integer queueId,
      Authentication authentication) {
      
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Turn calledTurn = turnManagementService.callNextTurn(queueId, userDetails.getId());
    
    return ResponseEntity.ok(calledTurn);
  }

  /**
   * Endpoint para marcar un turno como completado/atendido.
   * Requiere rol de OPERATOR. La validación de si el operario puede modificar este
   * turno específico se delega a la capa de servicio o se podría añadir aquí.
   *
   * @param turnId El ID del turno a completar.
   * @param authentication El objeto de autenticación del operario.
   * @return Una ResponseEntity con el Turno actualizado.
   */
  @PostMapping("/operator/turns/{turnId}/complete")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ResponseEntity<Turn> completeTurn(
      @PathVariable Long turnId,
      Authentication authentication) {

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Turn completedTurn = turnManagementService.completeTurn(turnId, userDetails.getId());
    
    return ResponseEntity.ok(completedTurn);
  }

  /**
   * Endpoint para marcar un turno como ausente (el cliente no se presentó).
   * Requiere rol de OPERATOR.
   *
   * @param turnId El ID del turno a marcar como ausente.
   * @param authentication El objeto de autenticación del operario.
   * @return Una ResponseEntity con el Turno actualizado.
   */
  @PostMapping("/operator/turns/{turnId}/mark-as-absent")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ResponseEntity<Turn> markAsAbsent(
      @PathVariable Long turnId,
      Authentication authentication) {

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Turn absentTurn = turnManagementService.markAsAbsent(turnId, userDetails.getId());

    return ResponseEntity.ok(absentTurn);
  }
}
