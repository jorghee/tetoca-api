package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.dto.TicketResponse;
import com.tetoca.tetoca_api.tenant.model.*;
import com.tetoca.tetoca_api.tenant.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnService {
    
    private final TurnRepository turnRepository;
    private final QueueRepository queueRepository;
    private final QueueRegistrationRepository queueRegistrationRepository;
    private final CompanyClientRepository clientRepository;
    private final TurnStatusRepository turnStatusRepository;
    
    @Transactional
    public TicketResponse joinQueue(Integer queueId, Integer clientId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Cola no encontrada"));
        
        if (!"A".equals(queue.getRecordStatus()) || 
            !"A".equals(queue.getAgency().getRecordStatus())) {
            throw new RuntimeException("No se puede unir a esta cola. La cola no está activa o la empresa no está disponible");
        }
        
        CompanyClient client = null;
        if (clientId != null) {
            client = clientRepository.findById(clientId).orElse(null);
            
            // Verificar si el cliente ya tiene un turno activo en esta cola
            boolean hasActiveTicket = turnRepository.findActiveTurnsByCompanyClientId(clientId)
                    .stream()
                    .anyMatch(turn -> turn.getQueueRegistration().getQueue().getId().equals(queueId));
            
            if (hasActiveTicket) {
                throw new RuntimeException("Ya tienes un ticket activo en esta cola");
            }
        }
        
        // Crear registro de cola
        Long currentTime = Instant.now().toEpochMilli();
        QueueRegistration queueRegistration = new QueueRegistration();
        queueRegistration.setQueue(queue);
        queueRegistration.setCompanyClient(client);
        queueRegistration.setRegistrationDateTime(currentTime);
        queueRegistration.setRegistrationMethod("APP");
        queueRegistration.setRecordStatus("A");
        queueRegistration = queueRegistrationRepository.save(queueRegistration);
        
        // Generar número de orden
        Integer maxOrderNumber = turnRepository.findMaxOrderNumberByQueueId(queueId);
        Integer newOrderNumber = (maxOrderNumber != null) ? maxOrderNumber + 1 : 1;
        
        // Obtener estado "WAITING"
        TurnStatus waitingStatus = turnStatusRepository.findByName("WAITING")
                .orElseThrow(() -> new RuntimeException("Estado WAITING no encontrado"));
        
        // Crear nuevo turno
        Turn turn = new Turn();
        turn.setQueueRegistration(queueRegistration);
        turn.setTurnStatus(waitingStatus);
        turn.setOrderNumber(newOrderNumber);
        turn.setGenerationDateTime(currentTime);
        turn.setRecordStatus("A");
        
        turn = turnRepository.save(turn);
        
        return mapToTicketResponse(turn);
    }
    
    public TicketResponse getTurnDetails(String turnCode) {
        // Extraer número de orden del código (formato: "NombreCola-001")
        String[] parts = turnCode.split("-");
        if (parts.length != 2) {
            throw new RuntimeException("Formato de ticket inválido");
        }
        
        try {
            Integer orderNumber = Integer.parseInt(parts[1]);
            // Buscar por nombre de cola y número de orden
            // Esta es una simplificación - idealmente necesitarías el ID de la cola
            Turn turn = turnRepository.findByQueueIdOrderByOrderNumber(null).stream()
                    .filter(t -> t.getOrderNumber().equals(orderNumber) && 
                               t.getQueueRegistration().getQueue().getName().equals(parts[0]))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado o expirado"));
            
            if (!"A".equals(turn.getRecordStatus())) {
                throw new RuntimeException("Ticket no encontrado o expirado");
            }
            
            return mapToTicketResponse(turn);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Formato de ticket inválido");
        }
    }
    
    @Transactional
    public TicketResponse pauseTurn(String turnCode) {
        // Similar lógica de búsqueda que getTurnDetails
        // Por simplicidad, asumo que tienes un método para encontrar el turno
        Turn turn = findTurnByCode(turnCode);
        
        if (!"WAITING".equals(turn.getTurnStatus().getName())) {
            throw new RuntimeException("El ticket ya está pausado o no puede ser pausado");
        }
        
        TurnStatus pausedStatus = turnStatusRepository.findByName("PAUSED")
                .orElseThrow(() -> new RuntimeException("Estado PAUSED no encontrado"));
        
        turn.setTurnStatus(pausedStatus);
        turn = turnRepository.save(turn);
        
        // Crear evento de turno
        createTurnEvent(turn, "WAITING", "PAUSED", "Usuario pausó el turno");
        
        TicketResponse response = mapToTicketResponse(turn);
        response.setMessage("Ticket pausado exitosamente");
        return response;
    }
    
    @Transactional
    public TicketResponse resumeTurn(String turnCode) {
        Turn turn = findTurnByCode(turnCode);
        
        if (!"PAUSED".equals(turn.getTurnStatus().getName())) {
            throw new RuntimeException("El ticket no está pausado o no puede ser reanudado");
        }
        
        // Mover al final de la cola
        Integer maxOrderNumber = turnRepository.findMaxOrderNumberByQueueId(
                turn.getQueueRegistration().getQueue().getId());
        turn.setOrderNumber((maxOrderNumber != null) ? maxOrderNumber + 1 : 1);
        
        TurnStatus waitingStatus = turnStatusRepository.findByName("WAITING")
                .orElseThrow(() -> new RuntimeException("Estado WAITING no encontrado"));
        
        turn.setTurnStatus(waitingStatus);
        turn = turnRepository.save(turn);
        
        // Crear evento de turno
        createTurnEvent(turn, "PAUSED", "WAITING", "Usuario reanudó el turno");
        
        TicketResponse response = mapToTicketResponse(turn);
        response.setMessage("Ticket reanudado exitosamente");
        return response;
    }
    
    @Transactional
    public void cancelTurn(String turnCode, String reason) {
        Turn turn = findTurnByCode(turnCode);
        
        if ("CANCELLED".equals(turn.getTurnStatus().getName()) || 
            "SERVED".equals(turn.getTurnStatus().getName())) {
            throw new RuntimeException("Ticket no encontrado o ya ha sido cancelado");
        }
        
        String previousStatus = turn.getTurnStatus().getName();
        
        TurnStatus cancelledStatus = turnStatusRepository.findByName("CANCELLED")
                .orElseThrow(() -> new RuntimeException("Estado CANCELLED no encontrado"));
        
        turn.setTurnStatus(cancelledStatus);
        turnRepository.save(turn);
        
        // Crear evento de turno
        createTurnEvent(turn, previousStatus, "CANCELLED", reason != null ? reason : "Cancelado por usuario");
    }
    
    private Turn findTurnByCode(String turnCode) {
        // Implementación simplificada - en producción necesitarías una mejor lógica
        String[] parts = turnCode.split("-");
        if (parts.length != 2) {
            throw new RuntimeException("Formato de ticket inválido");
        }
        
        try {
            Integer orderNumber = Integer.parseInt(parts[1]);
            // Esta es una búsqueda simplificada
            return turnRepository.findAll().stream()
                    .filter(t -> t.getOrderNumber().equals(orderNumber) && 
                               t.getQueueRegistration().getQueue().getName().equals(parts[0]) &&
                               "A".equals(t.getRecordStatus()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Formato de ticket inválido");
        }
    }
    
    private void createTurnEvent(Turn turn, String previousStatus, String newStatus, String reason) {
        // Crear evento en A3H_EVENTO_TURNO
        TurnEvent event = new TurnEvent();
        event.setTurn(turn);
        
        // Buscar estados
        TurnStatus prevStatus = turnStatusRepository.findByName(previousStatus).orElse(null);
        TurnStatus newStat = turnStatusRepository.findByName(newStatus).orElse(null);
        
        event.setPreviousStatus(prevStatus);
        event.setNewStatus(newStat);
        event.setEventDateTime(Instant.now().toEpochMilli());
        event.setReason(reason);
        event.setRecordStatus("A");
        
        // Guardar evento (necesitarías inyectar TurnEventRepository)
    }
    
    private TicketResponse mapToTicketResponse(Turn turn) {
        Queue queue = turn.getQueueRegistration().getQueue();
        
        // Generar código de turno
        String ticketCode = queue.getName() + "-" + String.format("%03d", turn.getOrderNumber());
        
        // Calcular posición actual en la cola
        Integer currentPosition = turnRepository.countTurnsAheadInQueue(
                queue.getId(), turn.getOrderNumber()) + 1;
        
        // Calcular tiempo de espera estimado
        Integer avgTime = queue.getEstimatedTimePerTurn() != null ? queue.getEstimatedTimePerTurn() : 5;
        Integer waitTimeMinutes = currentPosition * avgTime;
        String waitTime = waitTimeMinutes + " min";
        String peopleTime = avgTime + " min";
        
        // Obtener turno actual
        List<Turn> currentTurns = queueRepository.findCurrentServingTurn(queue.getId());
        String currentTicket = null;
        if (!currentTurns.isEmpty()) {
            Turn currentTurn = currentTurns.get(0);
            currentTicket = queue.getName() + "-" + String.format("%03d", currentTurn.getOrderNumber());
        }
        
        // Formatear fecha y hora
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(turn.getGenerationDateTime()), ZoneId.systemDefault());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        
        return TicketResponse.builder()
                .id(turn.getId().toString())
                .ticketId(ticketCode)
                .queueId(queue.getId().toString())
                .enterpriseId(queue.getAgency().getId().toString())
                .enterpriseName(queue.getAgency().getName())
                .queueName(queue.getName())
                .issueDate(dateTime.format(dateFormatter))
                .issueTime(dateTime.format(timeFormatter))
                .status(turn.getTurnStatus().getName().toLowerCase())
                .position(currentPosition)
                .currentTicket(currentTicket)
                .waitTime(waitTime)
                .peopleTime(peopleTime)
                .build();
    }
}
