package com.tetoca.tetoca_api.global.controller;

import com.tetoca.tetoca_api.global.dto.company.CompanyRegistrationRequest;
import com.tetoca.tetoca_api.global.dto.company.CompanyRequest;
import com.tetoca.tetoca_api.global.dto.company.CompanyResponse;
import com.tetoca.tetoca_api.global.service.CompanyManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SAAS_ADMIN')")
public class CompanyController {

  private final CompanyManagementService companyManagementService;

  @PostMapping
  public ResponseEntity<CompanyResponse> registerCompany(
    @Valid @RequestBody CompanyRegistrationRequest request
  ) {
    CompanyResponse response = companyManagementService.registerCompany(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Integer id) {
    CompanyResponse response = companyManagementService.getCompanyById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
    List<CompanyResponse> responses = companyManagementService.getAllCompanies();
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CompanyResponse> updateCompany(
    @PathVariable Integer id,
    @Valid @RequestBody CompanyRequest request
  ) {
    CompanyResponse response = companyManagementService.updateCompany(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
    companyManagementService.deleteCompany(id);
    return ResponseEntity.noContent().build();
  }
}
