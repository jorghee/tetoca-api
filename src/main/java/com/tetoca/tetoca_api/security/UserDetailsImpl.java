package com.tetoca.tetoca_api.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tetoca.tetoca_api.tenant.model.Worker;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

/**
 * Implementación de la interfaz UserDetails de Spring Security.
 * Contiene la información de seguridad esencial del usuario (trabajador)
 * recuperada de la base de datos para que Spring Security pueda realizar
 * la autenticación y autorización.
 */
@Getter
public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final Integer id;
  private final String fullName;
  private final String email;

  @JsonIgnore
  private final String password;

  private final String recordStatus;

  private final Collection<? extends GrantedAuthority> authorities; // Roles

  public UserDetailsImpl(Integer id, String fullName, String email, String password,
                         String recordStatus, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.password = password;
    this.recordStatus = recordStatus;
    this.authorities = authorities;
  }

  /**
   * Método de fábrica estático para construir un UserDetailsImpl a partir de una
   * entidad Worker y una colección de roles. Este es un patrón limpio para
   * desacoplar la creación del objeto de su uso.
   *
   * @param worker La entidad del trabajador desde la base de datos.
   * @param authorities La colección de roles/autoridades calculada para este trabajador.
   * @return una nueva instancia de UserDetailsImpl.
   */
  public static UserDetailsImpl build(Worker worker, Collection<? extends GrantedAuthority> authorities) {
    return new UserDetailsImpl(
        worker.getId(),
        worker.getFullName(),
        worker.getEmail(),
        worker.getPassword(),
        worker.getRecordStatus(),
        authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() { return password; }

  // Usamos el email como el identificador único para el login.
  @Override
  public String getUsername() { return email; }

  // La lógica de expiración de cuentas no está implementada, por lo que siempre es true.
  @Override
  public boolean isAccountNonExpired() { return true; }

  // La lógica de bloqueo de cuentas no está implementada, por lo que siempre es true.
  @Override
  public boolean isAccountNonLocked() { return true; }

  // La lógica de expiración de credenciales no está implementada, por lo que siempre es true.
  @Override
  public boolean isCredentialsNonExpired() { return true; }

  // La cuenta está habilitada si su estado de registro es 'A' (Activo).
  @Override
  public boolean isEnabled() { return "A".equalsIgnoreCase(this.recordStatus); }

  // Es una buena práctica implementar equals y hashCode basados en un identificador único.
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl that = (UserDetailsImpl) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() { return Objects.hash(id); }
}
