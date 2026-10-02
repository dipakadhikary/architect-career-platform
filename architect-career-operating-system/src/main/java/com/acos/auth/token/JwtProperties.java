package com.acos.auth.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT and refresh-token configuration bound from {@code acos.jwt.*}.
 *
 * @param secret HMAC signing secret (minimum 32 characters / 256 bits)
 * @param issuer token issuer claim
 * @param accessTokenTtl access token time-to-live
 * @param refreshTokenTtl refresh token time-to-live
 */
@Validated
@ConfigurationProperties(prefix = "acos.jwt")
public record JwtProperties(
    @NotBlank String secret,
    @NotBlank String issuer,
    @NotNull Duration accessTokenTtl,
    @NotNull Duration refreshTokenTtl) {

  /**
   * Validates property invariants after binding.
   *
   * @param secret HMAC secret
   * @param issuer issuer claim
   * @param accessTokenTtl access TTL
   * @param refreshTokenTtl refresh TTL
   */
  public JwtProperties {
    if (secret != null && secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
      throw new IllegalArgumentException("acos.jwt.secret must be at least 32 bytes");
    }
    if (accessTokenTtl != null && (accessTokenTtl.isZero() || accessTokenTtl.isNegative())) {
      throw new IllegalArgumentException("acos.jwt.access-token-ttl must be positive");
    }
    if (refreshTokenTtl != null && (refreshTokenTtl.isZero() || refreshTokenTtl.isNegative())) {
      throw new IllegalArgumentException("acos.jwt.refresh-token-ttl must be positive");
    }
  }
}
