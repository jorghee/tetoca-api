package com.tetoca.tetoca_api.security.service;

import com.tetoca.tetoca_api.global.model.Client;
import com.tetoca.tetoca_api.global.service.ClientManagementService;
import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import com.tetoca.tetoca_api.security.ClientDetailsImpl;
import com.tetoca.tetoca_api.security.dto.AuthResponse;
import com.tetoca.tetoca_api.security.dto.LoginRequest;
import com.tetoca.tetoca_api.security.dto.OAuthRequest;
import com.tetoca.tetoca_api.security.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
  
  private final AuthenticationManager authenticationManager;
  private final UserDetailsServiceImpl userDetailsService;
  private final JwtService jwtService;
  private final OAuthStrategyFactory oAuthStrategyFactory;
  private final ClientManagementService clientManagementService;

  public AuthResponse loginSaaSAdmin(LoginRequest request) {
    TenantContextHolder.clear();
    String prefixedUsername = "saas:" + request.getUsername();
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(prefixedUsername, request.getPassword())
    );

    String jwt = jwtService.generateToken(prefixedUsername, Collections.emptyMap());
    return AuthResponse.builder().token(jwt).build();
  }

  public AuthResponse loginWorker(LoginRequest request, String tenantId) {
    TenantContextHolder.setTenantId(tenantId);
    String prefixedUsername = "worker:" + request.getUsername();
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(prefixedUsername, request.getPassword())
    );

    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("tenantId", tenantId);

    String jwt = jwtService.generateToken(prefixedUsername, extraClaims);

    TenantContextHolder.clear();
    return AuthResponse.builder().token(jwt).build();
  }

  public AuthResponse loginOrRegisterClient(OAuthRequest request) {
    OAuthValidationService validationService = oAuthStrategyFactory.findStrategy(request.getProvider());
    OAuthUserInfo userInfo = validationService.validateAndExtractUserInfo(request.getToken());
    Client client = clientManagementService.getOrCreateClient(userInfo);

    // Construye el subject del JWT con el formato esperado "oauth:externalUid"
    String jwtSubject = "oauth:" + client.getExternalUid();
    String jwt = jwtService.generateToken(jwtSubject, Collections.emptyMap());
    
    return AuthResponse.builder().token(jwt).build();
  }
}
