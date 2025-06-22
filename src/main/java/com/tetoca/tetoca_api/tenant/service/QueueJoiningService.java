package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.exception.QueueClosedException;
import com.tetoca.tetoca_api.tenant.model.*;
import com.tetoca.tetoca_api.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueJoiningService {

  private final String QUEUE_OPEN = "ABIERTA";

  private final QueueRepository queueRepository;
  private final CompanyClientRepository clientRepository;
  private final FcmTokenService fcmTokenService;
  private final QueueRegistrationRepository registrationRepository;
  private final TurnRepository turnRepository;
  private final TurnStatusRepository turnStatusRepository;

  /**
   * Proceso completo para que un cliente se una a una cola.
   * Esta operación es atómica gracias a @Transactional.
   * @param queueId ID de la cola a la que se une.
   * @param clientExternalUid UID del cliente global.
   * @param fullName Nombre completo del cliente (para creación si no existe).
   * @param pushToken Token de notificación push del dispositivo.
   * @return El turno creado para el cliente.
   */
  @Transactional
  public Turn joinQueue(Integer queueId, String clientExternalUid, String fullName, String pushToken) {
    Queue queue = queueRepository.findById(queueId)
        .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", queueId));

    if (!QUEUE_OPEN.equalsIgnoreCase(queue.getQueueStatus().getName())) {
      throw new QueueClosedException("La cola '" + queue.getName() + "' no está abierta actualmente.");
    }

    CompanyClient client = getOrCreateCompanyClient(clientExternalUid, fullName);

    if (pushToken != null && !pushToken.isBlank()) {
      fcmTokenService.saveOrUpdateToken(client, pushToken);
    }

    QueueRegistration registration = createQueueRegistration(queue, client);

    return createTurn(registration);
  }

  private CompanyClient getOrCreateCompanyClient(String externalUid, String fullName) {
    return clientRepository.findByExternalUid(externalUid)
      .orElseGet(() -> {
        CompanyClient newClient = new CompanyClient();
        newClient.setExternalUid(externalUid);
        newClient.setFullName(fullName);
        newClient.setRegistrationDate((int) (System.currentTimeMillis() / 1000));
        return clientRepository.save(newClient);
      });
  }

  private QueueRegistration createQueueRegistration(Queue queue, CompanyClient client) {
    QueueRegistration registration = new QueueRegistration();
    registration.setQueue(queue);
    registration.setCompanyClient(client);
    registration.setRegistrationDateTime(System.currentTimeMillis());
    registration.setRegistrationMethod("MOBILE_APP");

    return registrationRepository.save(registration);
  }

  private Turn createTurn(QueueRegistration registration) {
    Turn turn = new Turn();
    turn.setQueueRegistration(registration);

    Integer maxOrder = queueRepository.findMaxOrderNumberByQueueId(registration.getQueue().getId());
    turn.setOrderNumber(maxOrder + 1);

    TurnStatus waitingStatus = turnStatusRepository.findByName("EN_ESPERA")
      .orElseThrow(() -> new IllegalStateException("Estado de turno 'EN_ESPERA' no encontrado en la base de datos."));
    turn.setTurnStatus(waitingStatus);
    turn.setGenerationDateTime(System.currentTimeMillis());

    return turnRepository.save(turn);
  }
}
