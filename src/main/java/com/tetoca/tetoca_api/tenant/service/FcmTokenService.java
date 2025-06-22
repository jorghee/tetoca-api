package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.model.CompanyClient;
import com.tetoca.tetoca_api.tenant.model.FcmToken;
import com.tetoca.tetoca_api.tenant.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

  private final FcmTokenRepository fcmTokenRepository;

  /**
   * Guarda un nuevo token FCM o actualiza su estado si ya existe.
   * @param client El cliente al que pertenece el token.
   * @param tokenValue El valor del token.
   */
  public void saveOrUpdateToken(CompanyClient client, String tokenValue) {
    fcmTokenRepository.findByToken(tokenValue)
      .ifPresentOrElse(
          existingToken -> {
            existingToken.setRecordStatus("A");
            fcmTokenRepository.save(existingToken);
          },
          () -> {
            FcmToken newToken = new FcmToken();
            newToken.setCompanyClient(client);
            newToken.setToken(tokenValue);
            newToken.setRegistrationDateTime(System.currentTimeMillis());
            fcmTokenRepository.save(newToken);
          }
      );
  }
}
