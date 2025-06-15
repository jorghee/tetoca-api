package com.tetoca.tetoca_api.global.controller;

import com.tetoca.tetoca_api.global.dto.company.CompanyRegisterRequest;
import com.tetoca.tetoca_api.global.service.CompanyRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyRegistrationService companyRegistrationService;

  @PostMapping
  public ResponseEntity<Void> registerCompany(@RequestBody CompanyRegisterRequest request) {
    companyRegistrationService.registerCompany(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
