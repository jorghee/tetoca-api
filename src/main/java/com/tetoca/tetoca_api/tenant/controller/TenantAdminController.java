package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.tenant.dto.request.WorkerCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.WorkerResponse;
import com.tetoca.tetoca_api.tenant.service.WorkerManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/{tenantId}/admin")
@RequiredArgsConstructor
public class TenantAdminController {
  
  private final WorkerManagementService workerManagementService;

  /**
   * Endpoint para crear un Administrador de Empresa.
   * Esta acción puede ser realizada por el Administrador SaaS (para crear el primero)
   * o por un Admin Empresa existente (para crear otros).
   *
   * @param tenantId El ID del tenant donde se creará el administrador.
   * @param request  Los datos del nuevo trabajador/administrador.
   * @return Una ResponseEntity con los datos del administrador creado.
   */
  @PostMapping("/company-admins")
  @PreAuthorize("hasRole('SAAS_ADMIN') or hasRole('COMPANY_ADMIN')")
  public ResponseEntity<WorkerResponse> createCompanyAdmin(
    @PathVariable String tenantId,
    @Valid @RequestBody WorkerCreateRequest request
  ) {
    WorkerResponse response = workerManagementService.createCompanyAdmin(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
