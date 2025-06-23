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
    return authenticateAndGenerateToken(prefixedUsername, request.getPassword());
  }

  public AuthResponse loginWorker(LoginRequest request, String tenantId) {
    TenantContextHolder.setTenantId(tenantId);
    String prefixedUsername = "worker:" + request.getUsername();
    AuthResponse response = authenticateAndGenerateToken(prefixedUsername, request.getPassword());
    TenantContextHolder.clear();
    return response;
  }

  public AuthResponse loginOrRegisterClient(OAuthRequest request) {
    OAuthValidationService validationService = oAuthStrategyFactory.findStrategy(request.getProvider());
    OAuthUserInfo userInfo = validationService.validateAndExtractUserInfo(request.getToken());
    Client client = clientManagementService.getOrCreateClient(userInfo);
    UserDetails userDetails = ClientDetailsImpl.build(client);
    String jwt = jwtService.generateToken(userDetails);
    
    return AuthResponse.builder().token(jwt).build();
  }
  
  private AuthResponse authenticateAndGenerateToken(String username, String password) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(username, password)
    );

    // UserDetailsService ya se encarga de cambiar el contexto de BD si es un worker
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    String jwt = jwtService.generateToken(userDetails);
    return AuthResponse.builder().token(jwt).build();
  }
}
