package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.tenant.dto.request.AgencyCreateRequest; 
import com.tetoca.tetoca_api.tenant.dto.AgencyResponse;
import com.tetoca.tetoca_api.tenant.dto.request.DivisionCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.DivisionResponse;
import com.tetoca.tetoca_api.tenant.dto.request.WorkerCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.WorkerResponse;
import com.tetoca.tetoca_api.tenant.service.DivisionManagementService;
import com.tetoca.tetoca_api.tenant.service.AgencyManagementService; 
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
  private final DivisionManagementService divisionManagementService;
  private final AgencyManagementService agencyManagementService;

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

  /**
   * Endpoint para crear una nueva División y su primer Administrador de División.
   * Autorizado solo para Administradores de Empresa.
   *
   * @param tenantId  El ID del tenant.
   * @param request   Contiene los datos de la división y del nuevo administrador.
   * @return Una ResponseEntity con los datos de la división creada.
   */
  @PostMapping("/divisions")
  @PreAuthorize("hasRole('COMPANY_ADMIN')")
  public ResponseEntity<DivisionResponse> createDivision(
    @PathVariable String tenantId,
    @Valid @RequestBody DivisionCreateRequest request
  ) {
    DivisionResponse response = divisionManagementService.createDivisionWithAdmin(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Endpoint para crear un nuevo Administrador de División para una división existente.
   * Autorizado para Admins de Empresa o Admins de la misma División.
   *
   * @param divisionId El ID de la división a la que se asignará el nuevo admin.
   * @param request    Los datos del nuevo trabajador/administrador.
   * @return Una ResponseEntity con los datos del admin creado.
   */
  @PostMapping("/divisions/{divisionId}/admins")
  @PreAuthorize("hasRole('COMPANY_ADMIN') or @securityService.isWorkerAdminOfDivision(authentication, #divisionId)")
  public ResponseEntity<WorkerResponse> createDivisionAdmin(
    @PathVariable String tenantId,
    @PathVariable Integer divisionId,
    @Valid @RequestBody WorkerCreateRequest request
  ) {
    WorkerResponse response = workerManagementService.createDivisionAdmin(request, divisionId);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Endpoint para crear una nueva Agencia y su primer Administrador de Agencia.
   * Autorizado para Admins de Empresa o Admins de la División a la que pertenecerá la agencia.
   *
   * @param tenantId  El ID del tenant.
   * @param request   Contiene los datos de la agencia y de su nuevo administrador.
   * @return Una ResponseEntity con los datos de la agencia creada.
   */
  @PostMapping("/agencies")
  @PreAuthorize("hasRole('COMPANY_ADMIN') or @securityService.isWorkerAdminOfDivision(authentication, #request.agency.divisionId)")
  public ResponseEntity<AgencyResponse> createAgency(
    @PathVariable String tenantId,
    @Valid @RequestBody AgencyCreateRequest request
  ) {
    AgencyResponse response = agencyManagementService.createAgencyWithAdmin(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}
