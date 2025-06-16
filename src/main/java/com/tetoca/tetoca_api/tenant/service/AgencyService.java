package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.dto.EnterpriseResponse;
import com.tetoca.tetoca_api.tenant.dto.QueueResponse;
import com.tetoca.tetoca_api.tenant.model.Agency;
import com.tetoca.tetoca_api.tenant.repository.AgencyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {
    
    private final AgencyRepository agencyRepository;
    private final QueueService queueService;
    
    public Page<EnterpriseResponse> getAllAgencies(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return agencyRepository.findByRecordStatusOrderByName("A", pageable)
                .map(this::mapToEnterpriseResponse);
    }
    
    public EnterpriseResponse getAgencyById(Integer id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        
        if (!"A".equals(agency.getRecordStatus())) {
            throw new RuntimeException("Empresa no encontrada");
        }
        
        EnterpriseResponse response = mapToEnterpriseResponse(agency);
        // Agregar las colas
        List<QueueResponse> queues = queueService.getQueuesByAgencyId(id);
        response.setQueues(queues);
        
        return response;
    }
    
    public Page<EnterpriseResponse> searchAgencies(String searchText, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return agencyRepository.findBySearchTextAndActive(searchText, pageable)
                .map(this::mapToEnterpriseResponse);
    }
    
    private EnterpriseResponse mapToEnterpriseResponse(Agency agency) {
        Integer activeQueues = agencyRepository.countActiveQueuesByAgencyId(agency.getId());
        
        // Generar shortName a partir del nombre
        String shortName = generateShortName(agency.getName());
        
        return EnterpriseResponse.builder()
                .id(agency.getId().toString())
                .name(agency.getName())
                .shortName(shortName)
                .type("Agencia") // Tipo fijo o basado en división
                .logo(null) // No existe en el modelo actual
                .address(agency.getAddress())
                .schedule(null) // No existe en el modelo actual
                .phone(null) // No existe en el modelo actual
                .isAvailable("A".equals(agency.getRecordStatus()))
                .activeQueues(activeQueues)
                .build();
    }
    
    private String generateShortName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        
        String[] words = fullName.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, Math.min(3, words[0].length())).toUpperCase();
        }
        
        StringBuilder shortName = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                shortName.append(word.charAt(0));
            }
        }
        return shortName.toString().toUpperCase();
    }
}