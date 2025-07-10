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
  
  /**
   * Pausa un turno a petición del cliente, manteniendo su posición original en la cola.
   * @param turnId El ID del turno a pausar.
   * @param clientExternalUid El ID externo del cliente para validación.
   * @return El turno actualizado.
   */
  @Transactional
  public Turn pauseTurn(Long turnId, String clientExternalUid) {
    Turn turn = getTurnAndValidateClient(turnId, clientExternalUid);
    
    // Solo se pueden pausar turnos que estén en espera
    if (!"EN_ESPERA".equals(turn.getTurnStatus().getName())) {
      throw new IllegalStateException("Solo se pueden pausar turnos en estado EN_ESPERA.");
    }
    
    TurnStatus pausedStatus = getTurnStatusByName("PAUSADO");
    TurnStatus previousStatus = turn.getTurnStatus();
    
    // Almacenamos el número de orden original para restaurarlo luego
    Integer originalOrder = turn.getOrderNumber();
    
    turn.setTurnStatus(pausedStatus);
    Turn updatedTurn = turnRepository.save(turn);
    
    turnEventService.logTurnEvent(updatedTurn, previousStatus, pausedStatus,
        "Turno pausado por cliente. Posición original: " + originalOrder);
        
    return updatedTurn;
  }
  
  /**
   * Reanuda un turno pausado, restaurándolo a su posición original en la cola.
   * @param turnId El ID del turno a reanudar.
   * @param clientExternalUid El ID externo del cliente para validación.
   * @return El turno actualizado.
   */
  @Transactional
  public Turn resumeTurn(Long turnId, String clientExternalUid) {
    Turn turn = getTurnAndValidateClient(turnId, clientExternalUid);
    
    // Solo se pueden reanudar turnos que estén pausados
    if (!"PAUSADO".equals(turn.getTurnStatus().getName())) {
      throw new IllegalStateException("Solo se pueden reanudar turnos en estado PAUSADO.");
    }
    
    TurnStatus waitingStatus = getTurnStatusByName("EN_ESPERA");
    TurnStatus previousStatus = turn.getTurnStatus();
    
    turn.setTurnStatus(waitingStatus);
    
    // Restauramos la posición original en la cola
    // Esto implica reorganizar los números de orden de los demás turnos si es necesario
    turnRepository.shiftOrderNumbersToAccommodate(
        turn.getQueueRegistration().getQueue().getId(), 
        turn.getOrderNumber(), 
        waitingStatus.getName());
    
    Turn updatedTurn = turnRepository.save(turn);
    
    turnEventService.logTurnEvent(updatedTurn, previousStatus, waitingStatus,
        "Turno reanudado por cliente. Restaurado a posición original: " + turn.getOrderNumber());
        
    return updatedTurn;
  }
  
  /**
   * Cancela un turno a petición del cliente.
   * @param turnId El ID del turno a cancelar.
   * @param clientExternalUid El ID externo del cliente para validación.
   */
  @Transactional
  public void cancelTurn(Long turnId, String clientExternalUid) {
    Turn turn = getTurnAndValidateClient(turnId, clientExternalUid);
    
    // Solo se pueden cancelar turnos que no estén ya finalizados
    if ("ATENDIDO".equals(turn.getTurnStatus().getName()) || 
        "AUSENTE".equals(turn.getTurnStatus().getName())) {
      throw new IllegalStateException("No se puede cancelar un turno que ya ha sido atendido o marcado como ausente.");
    }
    
    TurnStatus canceledStatus = getTurnStatusByName("CANCELADO");
    TurnStatus previousStatus = turn.getTurnStatus();
    
    turn.setTurnStatus(canceledStatus);
    Turn updatedTurn = turnRepository.save(turn);
    
    turnEventService.logTurnEvent(updatedTurn, previousStatus, canceledStatus,
        "Turno cancelado por el cliente.");
  }
  
  /**
   * Método para obtener un turno y verificar que pertenezca al cliente indicado.
   * @param turnId El ID del turno.
   * @param clientExternalUid El ID externo del cliente.
   * @return El turno verificado.
   */
  @Transactional(readOnly = true)
  public Turn getTurnAndValidateClient(Long turnId, String clientExternalUid) {
    Turn turn = turnRepository.findById(turnId)
        .orElseThrow(() -> new ResourceNotFoundException("Turn", "id", turnId));
    
    // Validar que el turno pertenece al cliente que hace la solicitud
    String turnClientUid = turn.getQueueRegistration().getCompanyClient().getExternalUid();
    if (!clientExternalUid.equals(turnClientUid)) {
      throw new IllegalStateException("El turno no pertenece al cliente autenticado.");
    }
    
    return turn;
  }
  
  /**
   * Cuenta cuántos turnos hay por delante de un número de orden específico en una cola.
   * 
   * @param queueId El ID de la cola.
   * @param orderNumber El número de orden desde el cual contar.
   * @return El número de turnos que están por delante.
   */
  @Transactional(readOnly = true)
  public Integer countTurnsAhead(Integer queueId, Integer orderNumber) {
    return (int) turnRepository.countByQueueRegistration_Queue_IdAndOrderNumberLessThanAndTurnStatus_NameAndRecordStatus(
        queueId, orderNumber, "EN_ESPERA", "A");
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
