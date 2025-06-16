package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.tenant.dto.EnterpriseResponse;
import com.tetoca.tetoca_api.tenant.service.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnterpriseController {
    
    private final AgencyService agencyService;
    
    @GetMapping("/enterprises")
    public ResponseEntity<Map<String, Object>> getAllEnterprises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        
        Page<EnterpriseResponse> enterprises = agencyService.getAllAgencies(page, limit);
        
        Map<String, Object> pagination = Map.of(
                "page", page,
                "limit", limit,
                "totalPages", enterprises.getTotalPages(),
                "totalRecords", enterprises.getTotalElements()
        );
        
        return ResponseEntity.ok(Map.of(
                "data", enterprises.getContent(),
                "pagination", pagination
        ));
    }
    
    @GetMapping("/enterprises/{enterpriseId}")
    public ResponseEntity<EnterpriseResponse> getEnterpriseById(@PathVariable Integer enterpriseId) {
        EnterpriseResponse enterprise = agencyService.getAgencyById(enterpriseId);
        return ResponseEntity.ok(enterprise);
    }
    
    @GetMapping("/enterprises/search")
    public ResponseEntity<Map<String, Object>> searchEnterprises(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        
        Page<EnterpriseResponse> enterprises = agencyService.searchAgencies(q, page, limit);
        
        Map<String, Object> pagination = Map.of(
                "page", page,
                "limit", limit,
                "totalPages", enterprises.getTotalPages(),
                "totalRecords", enterprises.getTotalElements()
        );
        
        return ResponseEntity.ok(Map.of(
                "data", enterprises.getContent(),
                "pagination", pagination
        ));
    }
}