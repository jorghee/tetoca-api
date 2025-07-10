package com.tetoca.tetoca_api.security.filter;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import com.tetoca.tetoca_api.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // Si no hay cabecera de autorización o no empieza con "Bearer ", pasamos al siguiente filtro.
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);
    username = jwtService.extractUsername(jwt);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      boolean isWorker = username.startsWith("worker:");
      if (isWorker) {
        String tenantId = jwtService.extractTenantId(jwt);
        if (tenantId == null) {
          // Token de trabajador inválido sin tenantId, no se puede continuar.
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
            userDetails, null, userDetails.getAuthorities()
          );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } finally {
        if (isWorker) TenantContextHolder.clear();
      }
    }
    filterChain.doFilter(request, response);
  }
}
