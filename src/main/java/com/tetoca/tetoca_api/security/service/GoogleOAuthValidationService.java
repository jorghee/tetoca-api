package com.tetoca.tetoca_api.security.service;

import com.tetoca.tetoca_api.security.dto.OAuthUserInfo;
import com.tetoca.tetoca_api.security.exception.InvalidOAuthTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuthValidationService implements OAuthValidationService {

  private final WebClient.Builder webClientBuilder;

  @Value("${application.security.oauth.google.client-id}")
  private String googleClientId;

  @Override
  public OAuthUserInfo validateAndExtractUserInfo(String idToken) {
    String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
    
    // Usamos WebClient de forma asíncrona y manejamos errores de forma reactiva
    return webClientBuilder.build()
      .get()
      .uri(url)
      .retrieve()
      .bodyToMono(Map.class)
      .flatMap(response -> {
        if (response == null || !googleClientId.equals(response.get("aud"))) {
          return Mono.error(new InvalidOAuthTokenException("Invalid Google token or audience mismatch."));
        }

        String externalUid = getProviderName() + "_" + response.get("sub").toString();
        
        return Mono.just(OAuthUserInfo.builder()
          .externalUid(externalUid)
          .email(response.get("email").toString())
          .fullName(response.get("name").toString())
          .provider(getProviderName())
          .build());
      })
      .onErrorMap(e -> !(e instanceof InvalidOAuthTokenException), 
          e -> new InvalidOAuthTokenException("Error validating Google token.", e))
      .block();
  }

  @Override
  public String getProviderName() { return "google"; }
}
