package com.acos.auth.security;

import com.acos.auth.entity.Role;
import com.acos.auth.entity.User;
import java.io.Serial;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Spring Security principal backed by an ACOS {@link User}. */
public final class AcosUserDetails implements UserDetails {

  @Serial private static final long serialVersionUID = 1L;

  private final UUID id;
  private final String email;
  private final String passwordHash;
  private final boolean enabled;
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * Creates user details.
   *
   * @param id user id
   * @param email email / username
   * @param passwordHash password hash
   * @param enabled enabled flag
   * @param authorities granted authorities
   */
  public AcosUserDetails(
      UUID id,
      String email,
      String passwordHash,
      boolean enabled,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.email = Objects.requireNonNull(email, "email must not be null");
    this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
    this.enabled = enabled;
    this.authorities =
        authorities == null ? java.util.List.of() : java.util.List.copyOf(authorities);
  }

  /**
   * Maps a persisted user to security user details.
   *
   * @param user persisted user with roles initialized
   * @return user details
   */
  public static AcosUserDetails from(User user) {
    Objects.requireNonNull(user, "user must not be null");
    Objects.requireNonNull(user.getId(), "user id must not be null");
    Collection<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(Role::getName)
            .map(roleType -> new SimpleGrantedAuthority("ROLE_" + roleType.name()))
            .collect(Collectors.toUnmodifiableSet());
    return new AcosUserDetails(
        user.getId(), user.getEmail(), user.getPasswordHash(), user.isEnabled(), authorities);
  }

  /**
   * Returns the persistent user identifier.
   *
   * @return user id
   */
  public UUID getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
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
    return enabled;
  }
}
