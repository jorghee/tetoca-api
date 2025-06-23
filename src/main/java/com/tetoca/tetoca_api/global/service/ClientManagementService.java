package com.tetoca.tetoca_api.global.service;

import com.tetoca.tetoca_api.global.model.Client;
import com.tetoca.tetoca_api.global.repository.ClientRepository;
import com.tetoca.tetoca_api.security.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ClientManagementService {

  private final ClientRepository clientRepository;

  /**
   * Obtiene un cliente existente por su UID externo o crea uno nuevo si no existe.
   * Esta operación es transaccional para garantizar la atomicidad.
   *
   * @param userInfo La información del usuario obtenida del proveedor OAuth.
   * @return La entidad Client, ya sea existente o recién creada.
   */
  @Transactional
  public Client getOrCreateClient(OAuthUserInfo userInfo) {
    return clientRepository.findByExternalUid(userInfo.getExternalUid())
      .map(client -> {
        boolean needsUpdate = false;
        if (!client.getName().equals(userInfo.getFullName())) {
          client.setName(userInfo.getFullName());
          needsUpdate = true;
        }
        if (needsUpdate) {
          return clientRepository.save(client);
        }
        return client;
      })
      .orElseGet(() -> {
        Client newClient = new Client();
        newClient.setExternalUid(userInfo.getExternalUid());
        newClient.setName(userInfo.getFullName());
        newClient.setEmail(userInfo.getEmail());
        newClient.setAuthProvider(userInfo.getProvider());
        newClient.setRegisterDate(Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
        newClient.setRecordStatus("A");
        
        return clientRepository.save(newClient);
      });
  }
}
