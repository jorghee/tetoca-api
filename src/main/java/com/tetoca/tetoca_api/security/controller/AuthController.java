package com.tetoca.tetoca_api.security.controller;

import com.tetoca.tetoca_api.security.dto.AuthResponse;
import com.tetoca.tetoca_api.security.dto.LoginRequest;
import com.tetoca.tetoca_api.security.dto.OAuthRequest;
import com.tetoca.tetoca_api.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  
  private final AuthService authService;
  
  @PostMapping("/admin/login")
  public ResponseEntity<AuthResponse> loginSaaSAdmin(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.loginSaaSAdmin(request));
  }
  
  @PostMapping("/worker/login/{tenantId}")
  public ResponseEntity<AuthResponse> loginWorker(
      @Valid @RequestBody LoginRequest request, 
      @PathVariable String tenantId
  ) {
    return ResponseEntity.ok(authService.loginWorker(request, tenantId));
  }

  @PostMapping("/client/oauth")
  public ResponseEntity<AuthResponse> loginClient(@Valid @RequestBody OAuthRequest request) {
    // El tenantId no es necesario aquí, ya que la autenticación del cliente es global.
    return ResponseEntity.ok(authService.loginOrRegisterClient(request));
  }
}
