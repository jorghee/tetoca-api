package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.EnterpriseResponse;
import com.tetoca.tetoca_api.tenant.dto.QueueResponse;
import com.tetoca.tetoca_api.tenant.model.Queue;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {
    
  private final QueueRepository queueRepository;
    
  /**
   * Obtiene la lista de colas activas para una agencia específica.
   * @param agencyId El ID de la agencia.
   * @return Una lista de DTOs QueueResponse.
   */
  @Transactional(readOnly = true)
  public List<QueueResponse> getActiveQueuesByAgencyId(Integer agencyId) {
    return queueRepository.findAllByAgency_IdAndRecordStatus(agencyId, "A").stream()
      .map(this::mapToQueueResponse)
      .collect(Collectors.toList());
  }
    
  /**
   * Obtiene los detalles de una cola específica por su ID.
   * @param queueId El ID de la cola.
   * @return Un DTO QueueResponse con detalles.
   */
  @Transactional(readOnly = true)
  public QueueResponse getQueueDetailsById(Integer queueId) {
    Queue queue = queueRepository.findByIdWithRelationships(queueId)
      .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", queueId));

    if (!"A".equals(queue.getRecordStatus())) {
      throw new ResourceNotFoundException("Queue", "id", queueId);
    }

    QueueResponse response = mapToQueueResponse(queue);
    
    // Agregar información de la agencia
    EnterpriseResponse enterprise = EnterpriseResponse.builder()
      .id(queue.getAgency().getId().toString())
      .name(queue.getAgency().getName())
      .shortName(generateShortName(queue.getAgency().getName()))
      .logo(null)
      .build();
    response.setEnterprise(enterprise);
    
    return response;
  }
    
  private QueueResponse mapToQueueResponse(Queue queue) {
    Integer peopleWaiting = queueRepository.countWaitingTurns(queue.getId());
    List<Turn> currentTurns = queueRepository.findCurrentServingTurn(queue.getId());

    Integer avgTime = queue.getEstimatedTimePerTurn() != null ? queue.getEstimatedTimePerTurn() : 5;
    String avgTimeStr = avgTime + " min";

    String currentTicket = null;
      
    if (!currentTurns.isEmpty()) {
      Turn currentTurn = currentTurns.get(0);
      // Asumimos un prefijo para el nombre de la cola si existe, si no, un genérico.
      String prefix = queue.getName() != null ? queue.getName().substring(0, 1) : "T";
      currentTicket = prefix + "-" + String.format("%03d", currentTurn.getOrderNumber());
    }
      
    boolean isActive = "ABIERTA".equalsIgnoreCase(queue.getQueueStatus().getName());
      
    return QueueResponse.builder()
      .id(queue.getId().toString())
      .name(queue.getName())
      .icon(null)
      .peopleWaiting(peopleWaiting)
      .avgTime(avgTimeStr)
      .enterpriseId(queue.getAgency().getId().toString())
      .isActive(isActive)
      .currentTicket(currentTicket)
      .waitTimePerPerson(avgTimeStr)
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
