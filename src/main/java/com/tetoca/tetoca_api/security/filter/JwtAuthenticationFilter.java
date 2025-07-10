package com.tetoca.tetoca_api.security.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import com.tetoca.tetoca_api.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // Si no hay cabecera de autorización o no empieza con "Bearer ", pasamos al
    // siguiente filtro.
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwt = authHeader.substring(7);

      // Verificar que el token no esté vacío
      if (jwt.trim().isEmpty()) {
        log.warn("Empty JWT token received");
        filterChain.doFilter(request, response);
        return;
      }

      username = jwtService.extractUsername(jwt);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        boolean isWorker = username.startsWith("worker:");
        if (isWorker) {
          String tenantId = jwtService.extractTenantId(jwt);
          if (tenantId == null) {
            log.warn("Worker token without tenantId: {}", username);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
          }
          // Establecemos el contexto del tenant ANTES de cualquier acceso a la BD.
          TenantContextHolder.setTenantId(tenantId);
        }

        try {
          UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

          if (jwtService.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        } catch (Exception e) {
          log.error("Error loading user details for username: {}", username, e);
          if (isWorker)
            TenantContextHolder.clear();
          // No devolver error, simplemente continuar sin autenticación
        } finally {
          if (isWorker)
            TenantContextHolder.clear();
        }
      }
    } catch (Exception e) {
      log.error("Error processing JWT token: {}", e.getMessage());
      // En caso de error, simplemente continuar sin autenticación
      // Spring Security manejará la falta de autenticación en endpoints protegidos
    }

    filterChain.doFilter(request, response);
  }
}
