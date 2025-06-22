package com.tetoca.tetoca_api.tenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/{tenantId}")
public class TenantPingController {

  @GetMapping("/ping")
  public ResponseEntity<String> ping(@PathVariable String tenantId) {
    return ResponseEntity.ok("Tenant database '" + tenantId + "' is reachable.");
  }
}
