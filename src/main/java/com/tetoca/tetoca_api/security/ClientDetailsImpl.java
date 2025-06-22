package com.tetoca.tetoca_api.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tetoca.tetoca_api.global.model.ClientEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class ClientDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final Integer id;
  private final String externalUid;
  private final String fullName;
  private final String email;
  private final boolean active;
  private final Collection<? extends GrantedAuthority> authorities;

  public ClientDetailsImpl(Integer id, String externalUid, String fullName, String email,
                           boolean active, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.externalUid = externalUid;
    this.fullName = fullName;
    this.email = email;
    this.active = active;
    this.authorities = authorities;
  }

  public static ClientDetailsImpl build(ClientEntity client) {
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));

    return new ClientDetailsImpl(
      client.getId(),
      client.getExternalUid(),
      client.getName(),
      client.getEmail(),
      "A".equalsIgnoreCase(client.getRecordStatus()),
      authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

  @Override
  public String getPassword() { return null; }

  @Override
  public String getUsername() { return this.email; }

  @Override
  public boolean isAccountNonExpired() { return true; }

  @Override
  public boolean isAccountNonLocked() { return true; }

  @Override
  public boolean isCredentialsNonExpired() { return true; }

  @Override
  public boolean isEnabled() { return this.active; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientDetailsImpl that = (ClientDetailsImpl) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() { return Objects.hash(id); }
}
