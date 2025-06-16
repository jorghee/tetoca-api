package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.dto.EnterpriseResponse;
import com.tetoca.tetoca_api.tenant.dto.QueueResponse;
import com.tetoca.tetoca_api.tenant.models.Queue;
import com.tetoca.tetoca_api.tenant.models.Turn;
import com.tetoca.tetoca_api.tenant.repositories.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {
    
    private final QueueRepository queueRepository;
    
    public List<QueueResponse> getQueuesByAgencyId(Integer agencyId) {
        return queueRepository.findActiveQueuesByAgencyId(agencyId).stream()
                .map(this::mapToQueueResponse)
                .collect(Collectors.toList());
    }
    
    public QueueResponse getQueueById(Integer queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Cola no encontrada"));
        
        if (!"A".equals(queue.getRecordStatus())) {
            throw new RuntimeException("Cola no encontrada");
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
        Integer avgTime = queue.getEstimatedTimePerTurn() != null ? queue.getEstimatedTimePerTurn() : 5;
        String avgTimeStr = avgTime + " min";
        
        // Obtener turno actual siendo atendido
        List<Turn> currentTurns = queueRepository.findCurrentServingTurn(queue.getId());
        String currentTicket = null;
        if (!currentTurns.isEmpty()) {
            Turn currentTurn = currentTurns.get(0);
            currentTicket = queue.getName() + "-" + String.format("%03d", currentTurn.getOrderNumber());
        }
        
        boolean isActive = "A".equals(queue.getRecordStatus()) && 
                          queue.getQueueStatus() != null && 
                          "A".equals(queue.getQueueStatus().getRecordStatus());
        
        return QueueResponse.builder()
                .id(queue.getId().toString())
                .name(queue.getName())
                .icon(null) // No existe en el modelo
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