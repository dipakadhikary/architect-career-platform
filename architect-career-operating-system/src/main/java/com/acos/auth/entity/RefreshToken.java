package com.acos.auth.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.time.Instant;
import java.util.Objects;

/** Persisted opaque refresh token identified by a SHA-256 hash. */
@Entity
@Table(
    name = "refresh_tokens",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_refresh_tokens_token_hash", columnNames = "token_hash"))
public class RefreshToken extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_refresh_tokens_user_id"))
  private User user;

  @Column(name = "token_hash", nullable = false, length = 64, updatable = false)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "revoked", nullable = false)
  private boolean revoked;

  /** Creates an empty refresh token for JPA. */
  protected RefreshToken() {}

  /**
   * Creates a refresh token for the given user.
   *
   * @param user owning user
   * @param tokenHash SHA-256 hex digest of the raw token
   * @param expiresAt expiration instant
   */
  public RefreshToken(User user, String tokenHash, Instant expiresAt) {
    this.user = Objects.requireNonNull(user, "user must not be null");
    this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash must not be null");
    this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    this.revoked = false;
  }

  /**
   * Returns the owning user.
   *
   * @return user
   */
  public User getUser() {
    return user;
  }

  /**
   * Returns the SHA-256 token hash.
   *
   * @return token hash
   */
  public String getTokenHash() {
    return tokenHash;
  }

  /**
   * Returns the expiration instant.
   *
   * @return expiration
   */
  public Instant getExpiresAt() {
    return expiresAt;
  }

  /**
   * Returns whether the token has been revoked.
   *
   * @return {@code true} when revoked
   */
  public boolean isRevoked() {
    return revoked;
  }

  /** Marks the token as revoked. */
  public void revoke() {
    this.revoked = true;
  }

  /**
   * Returns whether the token is usable at the given instant.
   *
   * @param instant evaluation instant
   * @return {@code true} when active
   */
  public boolean isActive(Instant instant) {
    Objects.requireNonNull(instant, "instant must not be null");
    return !revoked && expiresAt.isAfter(instant);
  }
}
