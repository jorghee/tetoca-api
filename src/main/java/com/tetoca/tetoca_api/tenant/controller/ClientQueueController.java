package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.security.ClientDetailsImpl;
import com.tetoca.tetoca_api.tenant.dto.request.JoinQueueRequest;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.service.QueueJoiningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/{tenantId}/queues")
@RequiredArgsConstructor
public class ClientQueueController {

  private final QueueJoiningService queueJoiningService;

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
}
