package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.EnterpriseResponse;
import com.tetoca.tetoca_api.tenant.dto.QueueResponse;
import com.tetoca.tetoca_api.tenant.model.Agency;
import com.tetoca.tetoca_api.tenant.repository.AgencyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {
    
  private final AgencyRepository agencyRepository;
  private final QueueService queueService;
    
  /**
   * Obtiene una página de agencias activas, transformadas en DTOs para la vista.
   * @param page Número de página.
   * @param limit Tamaño de la página.
   * @return Una página de EnterpriseResponse.
   */
  @Transactional(readOnly = true)
  public Page<EnterpriseResponse> getAllAgencies(int page, int limit) {
    Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit);
    return agencyRepository.findByRecordStatusOrderByName("A", pageable)
      .map(this::mapToEnterpriseResponse);
  }
    
  /**
   * Obtiene los detalles de una agencia por su ID, incluyendo sus colas activas.
   * @param id El ID de la agencia.
   * @return Un DTO EnterpriseResponse con todos los detalles.
   */
  @Transactional(readOnly = true)
  public EnterpriseResponse getAgencyDetailsById(Integer id) {
    Agency agency = agencyRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Agency", "id", id));

    if (!"A".equals(agency.getRecordStatus())) {
      throw new ResourceNotFoundException("Agency", "id", id);
    }

    EnterpriseResponse response = mapToEnterpriseResponse(agency);

    // Agregar las colas activas de la agencia
    List<QueueResponse> queues = queueService.getActiveQueuesByAgencyId(id);
    response.setQueues(queues);

    return response;
  }
   
  /**
   * Busca agencias activas por un texto de búsqueda y las devuelve paginadas.
   * @param searchText El texto a buscar en el nombre de la agencia.
   * @param page Número de página.
   * @param limit Tamaño de la página.
   * @return Una página de EnterpriseResponse.
   */
  @Transactional(readOnly = true)
  public Page<EnterpriseResponse> searchActiveAgencies(String searchText, int page, int limit) {
    Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit);
    return agencyRepository.findBySearchTextAndActive(searchText, pageable)
      .map(this::mapToEnterpriseResponse);
  }
    
  private EnterpriseResponse mapToEnterpriseResponse(Agency agency) {
    Integer activeQueues = agencyRepository.countActiveQueuesByAgencyId(agency.getId());
    String shortName = generateShortName(agency.getName());
    
    return EnterpriseResponse.builder()
      .id(agency.getId().toString())
      .name(agency.getName())
      .shortName(shortName)
      .type("Agencia")
      .logo(null)
      .address(agency.getAddress())
      .schedule(null)
      .phone(null)
      .isAvailable("A".equals(agency.getRecordStatus()))
      .activeQueues(activeQueues)
      .build();
  }
    
  private String generateShortName(String fullName) {
    if (fullName == null || fullName.trim().isEmpty()) { return ""; }
    
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
