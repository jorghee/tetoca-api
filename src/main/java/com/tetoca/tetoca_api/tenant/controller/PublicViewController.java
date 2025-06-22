package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.tenant.dto.CategoryResponse;
import com.tetoca.tetoca_api.tenant.dto.EnterpriseResponse;
import com.tetoca.tetoca_api.tenant.dto.QueueResponse;
import com.tetoca.tetoca_api.tenant.service.AgencyService;
import com.tetoca.tetoca_api.tenant.service.CategoryService;
import com.tetoca.tetoca_api.tenant.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para exponer la información pública de un tenant.
 * Agrupa endpoints para listar agencias (empresas), categorías (divisiones) y colas.
 * No requiere autenticación.
 */
@RestController
@RequestMapping("/api/tenant/{tenantId}/public")
@RequiredArgsConstructor
public class PublicViewController {

  private final AgencyService agencyService;
  private final QueueService queueService;
  private final CategoryService categoryService;

  // Endpoints de Agencias (Enterprises)

  @GetMapping("/agencies")
  public ResponseEntity<Page<EnterpriseResponse>> getAllAgencies(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int limit) {
    Page<EnterpriseResponse> agencies = agencyService.getAllActiveAgencies(page, limit);
    return ResponseEntity.ok(agencies);
  }

  @GetMapping("/agencies/search")
  public ResponseEntity<Page<EnterpriseResponse>> searchAgencies(
    @RequestParam String q,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int limit) {
    Page<EnterpriseResponse> agencies = agencyService.searchActiveAgencies(q, page, limit);
    return ResponseEntity.ok(agencies);
  }

  @GetMapping("/agencies/{agencyId}")
  public ResponseEntity<EnterpriseResponse> getAgencyDetails(@PathVariable Integer agencyId) {
    EnterpriseResponse agency = agencyService.getAgencyDetailsById(agencyId);
    return ResponseEntity.ok(agency);
  }

  // Endpoints de Colas (Queues)

  @GetMapping("/agencies/{agencyId}/queues")
  public ResponseEntity<List<QueueResponse>> getQueuesByAgency(@PathVariable Integer agencyId) {
    List<QueueResponse> queues = queueService.getActiveQueuesByAgencyId(agencyId);
    return ResponseEntity.ok(queues);
  }

  @GetMapping("/queues/{queueId}")
  public ResponseEntity<QueueResponse> getQueueDetails(@PathVariable Integer queueId) {
    QueueResponse queue = queueService.getQueueDetailsById(queueId);
    return ResponseEntity.ok(queue);
  }

  // Endpoints de Categorías (Divisions)

  @GetMapping("/categories")
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    List<CategoryResponse> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categories);
  }
}
