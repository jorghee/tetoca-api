package com.tetoca.tetoca_api.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tetoca.tetoca_api.global.model.SaaSAdmin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class SaaSAdminDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private final Integer id;
  private final String fullName;
  private final String email;

  @JsonIgnore
  private final String password;

  private final String recordStatus;
  private final Collection<? extends GrantedAuthority> authorities;

  public SaaSAdminDetailsImpl(Integer id, String fullName, String email, String password,
                              String recordStatus, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.password = password;
    this.recordStatus = recordStatus;
    this.authorities = authorities;
  }

  public static SaaSAdminDetailsImpl build(SaaSAdmin admin) {
    // El administrador de SaaS siempre tiene el rol 'SAAS_ADMIN'
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_SAAS_ADMIN"));

    return new SaaSAdminDetailsImpl(
      admin.getId(),
      admin.getFullName(),
      admin.getEmail(),
      admin.getPassword(),
      admin.getRecordStatus(),
      authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    // Usamos el email como identificador de login
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    // La cuenta está habilitada si su estado es 'A' (Activo)
    return "A".equalsIgnoreCase(this.recordStatus);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SaaSAdminDetailsImpl that = (SaaSAdminDetailsImpl) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
