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

/** Single-use password reset token identified by a SHA-256 hash. */
@Entity
@Table(
    name = "password_reset_tokens",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_password_reset_tokens_token_hash",
            columnNames = "token_hash"))
public class PasswordResetToken extends BaseEntity {

  /** Purpose stored for every row created by this feature. */
  public static final String PURPOSE_PASSWORD_RESET = "PASSWORD_RESET";

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_password_reset_tokens_user_id"))
  private User user;

  @Column(name = "token_hash", nullable = false, length = 64, updatable = false)
  private String tokenHash;

  @Column(name = "purpose", nullable = false, length = 32, updatable = false)
  private String purpose;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "used_at")
  private Instant usedAt;

  /** Creates an empty token for JPA. */
  protected PasswordResetToken() {}

  /**
   * Creates an unused password-reset token.
   *
   * @param user owning user
   * @param tokenHash SHA-256 hex digest of the raw token
   * @param expiresAt expiration instant
   */
  public PasswordResetToken(User user, String tokenHash, Instant expiresAt) {
    this.user = Objects.requireNonNull(user, "user must not be null");
    this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash must not be null");
    this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    this.purpose = PURPOSE_PASSWORD_RESET;
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
   * Returns the token purpose.
   *
   * @return purpose
   */
  public String getPurpose() {
    return purpose;
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
   * Returns when the token was consumed.
   *
   * @return use instant, or {@code null} when unused
   */
  public Instant getUsedAt() {
    return usedAt;
  }

  /**
   * Marks the token used when it is still unused and unexpired.
   *
   * @param now current instant
   * @return {@code true} when this call consumed the token
   */
  public synchronized boolean consume(Instant now) {
    Objects.requireNonNull(now, "now must not be null");
    if (usedAt != null || !expiresAt.isAfter(now)) {
      return false;
    }
    if (!PURPOSE_PASSWORD_RESET.equals(purpose)) {
      return false;
    }
    usedAt = now;
    return true;
  }

  /**
   * Marks an outstanding token used without checking expiry.
   *
   * @param now current instant
   */
  public void supersede(Instant now) {
    if (usedAt == null) {
      usedAt = Objects.requireNonNull(now, "now must not be null");
    }
  }
}
