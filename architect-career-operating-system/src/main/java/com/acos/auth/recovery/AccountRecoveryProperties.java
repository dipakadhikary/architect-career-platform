package com.acos.auth.recovery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Password-reset and login-identifier recovery settings.
 *
 * @param tokenTtl lifetime of a password reset token
 * @param frontendBaseUrl trusted origin used to build the reset link
 * @param maxRequestsPerHour maximum recovery requests for one email in an hour
 * @param minInterval minimum time between recovery emails for one email
 */
@Validated
@ConfigurationProperties(prefix = "acos.recovery")
public record AccountRecoveryProperties(
    @NotNull Duration tokenTtl,
    @NotBlank String frontendBaseUrl,
    @Positive int maxRequestsPerHour,
    @NotNull Duration minInterval) {

  /**
   * Validates that the frontend origin cannot be chosen by a caller.
   *
   * @param tokenTtl token lifetime
   * @param frontendBaseUrl configured origin
   * @param maxRequestsPerHour hourly cap
   * @param minInterval cooldown
   */
  public AccountRecoveryProperties {
    if (tokenTtl != null && (tokenTtl.isZero() || tokenTtl.isNegative())) {
      throw new IllegalArgumentException("acos.recovery.token-ttl must be positive");
    }
    if (minInterval != null && minInterval.isNegative()) {
      throw new IllegalArgumentException("acos.recovery.min-interval must not be negative");
    }
    if (frontendBaseUrl != null && !frontendBaseUrl.isBlank()) {
      URI origin = URI.create(frontendBaseUrl);
      String scheme = origin.getScheme();
      if ((!"http".equals(scheme) && !"https".equals(scheme)) || origin.getUserInfo() != null) {
        throw new IllegalArgumentException("acos.recovery.frontend-base-url must be an http(s) origin");
      }
    }
  }
}
