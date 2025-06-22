package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.dto.ExpoPushMessage;
import com.tetoca.tetoca_api.tenant.model.Turn;
import com.tetoca.tetoca_api.tenant.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushNotificationService {

  private final FcmTokenRepository fcmTokenRepository;
  private final WebClient.Builder webClientBuilder;

  /**
   * Envía una notificación al cliente cuando su turno es llamado.
   * @param turn El turno que ha sido llamado.
   */
  public void sendTurnCalledNotification(Turn turn) {
    Integer clientId = turn.getQueueRegistration().getCompanyClient().getId();
    List<String> tokens = fcmTokenRepository.findAllActiveTokensByClientId(clientId);

    if (tokens.isEmpty()) {
      log.warn("No se encontraron tokens de notificación para el cliente con ID: {}", clientId);
      return;
    }

    String agencyName = turn.getQueueRegistration().getQueue().getAgency().getName();
    String title = "¡Es tu turno en " + agencyName + "!";
    String body = "Tu ticket " + turn.getQueueRegistration().getQueue().getName() + "-" + turn.getOrderNumber() + " está siendo llamado. Por favor, acércate.";

    // Datos adicionales para que la app cliente sepa a dónde navegar
    ExpoPushMessage.Data data = new ExpoPushMessage.Data(turn.getId(), turn.getQueueRegistration().getQueue().getId());
    ExpoPushMessage message = new ExpoPushMessage(tokens, title, body, data);

    // Enviar la notificación de forma asíncrona
    webClientBuilder.build().post()
      .uri("https://exp.host/--/api/v2/push/send")
      .header("Content-Type", "application/json")
      .bodyValue(message)
      .retrieve()
      .bodyToMono(String.class)
      .subscribe(
          response -> log.info("Notificación enviada con éxito. Respuesta de Expo: {}", response),
          error -> log.error("Error al enviar notificación a Expo: {}", error.getMessage())
      );
  }
}
