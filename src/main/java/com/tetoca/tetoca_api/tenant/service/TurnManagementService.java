package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.exception.NoTurnsAvailableException;
import com.tetoca.tetoca_api.tenant.model.Operator;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.model.TurnStatus;
import com.tetoca.tetoca_api.tenant.repository.OperatorRepository;
import com.tetoca.tetoca_api.tenant.repository.TurnRepository;
import com.tetoca.tetoca_api.tenant.repository.TurnStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TurnManagementService {

  private final TurnRepository turnRepository;
  private final TurnStatusRepository turnStatusRepository;
  private final OperatorRepository operatorRepository;
  private final ExpoPushNotificationService pushNotificationService;
  private final TurnEventService turnEventService;

  /**
   * Llama al siguiente turno en una cola.
   * @param queueId El ID de la cola.
   * @param operatorWorkerId El ID del trabajador (operario) que realiza la acción.
   * @return El turno que ha sido llamado.
   */
  @Transactional
  public Turn callNextTurn(Integer queueId, Integer operatorWorkerId) {
    Operator operator = operatorRepository.findById(operatorWorkerId)
        .orElseThrow(() -> new ResourceNotFoundException("Operator", "workerId", operatorWorkerId));

    Turn nextTurn = turnRepository.findNextTurnInQueue(queueId, "EN_ESPERA")
        .orElseThrow(() -> new NoTurnsAvailableException("No hay más turnos en espera en esta cola."));

    TurnStatus callingStatus = getTurnStatusByName("LLAMANDO");
    TurnStatus previousStatus = nextTurn.getTurnStatus();

    nextTurn.setTurnStatus(callingStatus);
    nextTurn.setOperator(operator);
    nextTurn.setAttentionDateTime(System.currentTimeMillis());
    Turn updatedTurn = turnRepository.save(nextTurn);
      
    turnEventService.logTurnEvent(updatedTurn, previousStatus, callingStatus,
        "Llamado por operador " + operator.getWorker().getFullName());

    pushNotificationService.sendTurnCalledNotification(updatedTurn);

    return updatedTurn;
  }

  /**
   * Finaliza un turno, marcándolo como "ATENDIDO".
   * @param turnId El ID del turno a finalizar.
   * @param operatorWorkerId El ID del operario.
   * @return El turno finalizado.
   */
  @Transactional
  public Turn completeTurn(Long turnId, Integer operatorWorkerId) {
    return updateTurnStatus(turnId, operatorWorkerId, "ATENDIDO", "Turno completado por operario.");
  }

  /**
   * Marca un turno como "AUSENTE".
   * @param turnId El ID del turno.
   * @param operatorWorkerId El ID del operario.
   * @return El turno actualizado.
   */
  @Transactional
  public Turn markAsAbsent(Long turnId, Integer operatorWorkerId) {
    return updateTurnStatus(turnId, operatorWorkerId, "AUSENTE", "Cliente no se presentó.");
  }
  
  // Método privado reutilizable para cambiar el estado de un turno
  private Turn updateTurnStatus(Long turnId, Integer operatorWorkerId, String newStatusName, String reason) {
    Turn turn = turnRepository.findById(turnId)
        .orElseThrow(() -> new ResourceNotFoundException("Turn", "id", turnId));
    
    // Opcional: Validar que el operario es el mismo que llamó al turno o tiene permiso.
    // if (!turn.getOperator().getWorkerId().equals(operatorWorkerId)) { ... }
    
    TurnStatus newStatus = getTurnStatusByName(newStatusName);
    TurnStatus previousStatus = turn.getTurnStatus();

    turn.setTurnStatus(newStatus);
    Turn updatedTurn = turnRepository.save(turn);
    
    turnEventService.logTurnEvent(updatedTurn, previousStatus, newStatus, reason);

    return updatedTurn;
  }

  private TurnStatus getTurnStatusByName(String name) {
    return turnStatusRepository.findByName(name)
        .orElseThrow(() -> new IllegalStateException("Estado de turno '" + name + "' no encontrado."));
  }
}
