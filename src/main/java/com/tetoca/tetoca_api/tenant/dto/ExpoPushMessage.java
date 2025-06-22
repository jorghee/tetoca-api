package com.tetoca.tetoca_api.tenant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO que representa el payload de una notificación push para la API de Expo.
 * Utiliza @Data de Lombok para generar automáticamente getters, setters, toString, etc.
 * @see <a href="https://docs.expo.dev/push-notifications/sending-notifications/#push-ticket-request-format">Expo Push Ticket Request Format</a>
 */
@Data
@NoArgsConstructor
public class ExpoPushMessage {

  /**
   * Una lista de tokens de destinatario (ExpoPushToken).
   */
  @JsonProperty("to")
  private List<String> to;

  /**
   * El título que se mostrará en la notificación.
   */
  @JsonProperty("title")
  private String title;

  /**
   * El cuerpo del mensaje de la notificación.
   */
  @JsonProperty("body")
  private String body;

  /**
   * Un objeto JSON de datos adicionales que se enviarán a la aplicación cliente.
   * Útil para la navegación interna o para procesar la notificación.
   */
  @JsonProperty("data")
  private Data data;

  /**
   * El sonido a reproducir. "default" para el sonido de notificación predeterminado del sistema.
   */
  @JsonProperty("sound")
  private String sound = "default";

  /**
   * La prioridad de entrega de la notificación. Puede ser "default", "normal", o "high".
   */
  @JsonProperty("priority")
  private String priority = "default";

  /**
   * El número a mostrar en el ícono de la aplicación (badge) en iOS.
   */
  @JsonProperty("badge")
  private Integer badge;

  /**
   * Constructor de conveniencia para los campos más comunes.
   * @param to Lista de tokens de destinatario.
   * @param title Título de la notificación.
   * @param body Cuerpo del mensaje.
   * @param data Datos adicionales.
   */
  public ExpoPushMessage(List<String> to, String title, String body, Data data) {
    this.to = to;
    this.title = title;
    this.body = body;
    this.data = data;
  }

  /**
   * Clase anidada estática para representar el objeto "data".
   * Esto mantiene la estructura del DTO contenida y organizada.
   */
  @Getter
  @AllArgsConstructor
  public static class Data {
    /**
     * El ID del turno para que la app cliente sepa a qué turno se refiere la notificación.
     */
    @JsonProperty("turnId")
    private Long turnId;

    /**
     * El ID de la cola para contexto adicional.
     */
    @JsonProperty("queueId")
    private Integer queueId;
  }
}
