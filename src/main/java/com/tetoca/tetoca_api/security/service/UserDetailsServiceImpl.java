package com.tetoca.tetoca_api.security.service;

import com.tetoca.tetoca_api.global.model.Client;
import com.tetoca.tetoca_api.global.model.SaaSAdmin;
import com.tetoca.tetoca_api.global.repository.SaaSAdminRepository;
import com.tetoca.tetoca_api.global.service.ClientManagementService;
import com.tetoca.tetoca_api.security.ClientDetailsImpl;
import com.tetoca.tetoca_api.security.SaaSAdminDetailsImpl;
import com.tetoca.tetoca_api.security.UserDetailsImpl;
import com.tetoca.tetoca_api.security.dto.OAuthUserInfo;
import com.tetoca.tetoca_api.tenant.model.Worker;
import com.tetoca.tetoca_api.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  // repository and service global
  private final SaaSAdminRepository saasAdminRepository;
  private final ClientManagementService clientManagementService;

  // Tenant repositories
  private final WorkerRepository workerRepository;
  private final CompanyAdminRepository companyAdminRepository;
  private final DivisionAdminRepository divisionAdminRepository;
  private final AgencyAdminRepository agencyAdminRepository;
  private final OperatorRepository operatorRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    // El formato del username determinará dónde y qué buscar.
    // Ej: "saas:admin@tetoca.com", "worker:user@empresa.com", "oauth:google_12345"
    String[] parts = username.split(":", 2);
    if (parts.length != 2) {
      throw new UsernameNotFoundException("Invalid username format. Should be ‘type:identifier’.");
    }
    
    String userType = parts[0];
    String identifier = parts[1];
    
    switch (userType) {
      case "saas":
        return loadSaaSAdmin(identifier);
      case "worker":
        return loadWorker(identifier);
      case "oauth":
        // Este caso ahora es problemático porque `loadUserByUsername` no tiene suficiente
        // información (email, name) para crear un cliente. La lógica "get or create"
        // se manejará en AuthService, que sí tiene el OAuthUserInfo completo.
        // Aquí, simplemente lanzamos una excepción para forzar el flujo correcto.
        throw new UnsupportedOperationException("OAuth user loading is not done directly here. Use the AuthService flow.");
      default:
        throw new UsernameNotFoundException("User type not supported: " + userType);
    }
  }

  private UserDetails loadSaaSAdmin(String email) {
    SaaSAdmin admin = saasAdminRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Admin SaaS not found with email: " + email));
    return SaaSAdminDetailsImpl.build(admin);
  }

  private UserDetails loadWorker(String email) {
    Worker worker = workerRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Worker not found with email: " + email));

    Set<GrantedAuthority> authorities = new HashSet<>();
    companyAdminRepository.findByWorker(worker)
      .ifPresent(ca -> authorities.add(new SimpleGrantedAuthority("ROLE_COMPANY_ADMIN")));

    divisionAdminRepository.findByWorker_Id(worker.getId())
      .forEach(da -> authorities.add(new SimpleGrantedAuthority("ROLE_DIVISION_ADMIN")));

    agencyAdminRepository.findByWorker_Id(worker.getId())
      .forEach(aa -> authorities.add(new SimpleGrantedAuthority("ROLE_AGENCY_ADMIN")));
    
    operatorRepository.findById(worker.getId())
      .ifPresent(op -> authorities.add(new SimpleGrantedAuthority("ROLE_OPERATOR")));
   
    if (authorities.isEmpty()) {
      throw new UsernameNotFoundException("The worker " + email + " has no assigned roles");
    }
    
    return UserDetailsImpl.build(worker, authorities);
  }
}
