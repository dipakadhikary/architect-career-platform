package com.acos.integration.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Strongly typed configuration for the AI Platform integration bound from {@code ai.platform.*}.
 *
 * @param enabled whether outbound AI Platform calls are enabled
 * @param baseUrl AI Platform base URL (no trailing slash required)
 * @param apiKey API key sent to the AI Platform
 * @param connectionTimeout TCP connect timeout
 * @param readTimeout response read timeout
 * @param loggerLevel Feign request logger level ({@code NONE}, {@code BASIC}, {@code HEADERS},
 *     {@code FULL})
 * @param compression request/response compression settings
 * @param resilience Resilience4j instance name and related toggles
 * @param retry legacy retry block retained for property compatibility; Resilience4j owns retries
 */
@Validated
@ConfigurationProperties(prefix = "ai.platform")
public record AiPlatformProperties(
    boolean enabled,
    @NotBlank String baseUrl,
    String apiKey,
    @NotNull Duration connectionTimeout,
    @NotNull Duration readTimeout,
    @NotBlank String loggerLevel,
    @Valid @NotNull @NestedConfigurationProperty CompressionProperties compression,
    @Valid @NotNull @NestedConfigurationProperty ResilienceProperties resilience,
    @Valid @NotNull @NestedConfigurationProperty RetryProperties retry) {

  /**
   * Creates AI Platform properties with invariant checks.
   *
   * @param enabled whether integration is enabled
   * @param baseUrl base URL
   * @param apiKey API key (may be blank when disabled)
   * @param connectionTimeout connect timeout
   * @param readTimeout read timeout
   * @param loggerLevel Feign logger level name
   * @param compression compression settings
   * @param resilience resilience settings
   * @param retry retry settings
   */
  public AiPlatformProperties {
    if (connectionTimeout != null
        && (connectionTimeout.isZero() || connectionTimeout.isNegative())) {
      throw new IllegalArgumentException("ai.platform.connection-timeout must be positive");
    }
    if (readTimeout != null && (readTimeout.isZero() || readTimeout.isNegative())) {
      throw new IllegalArgumentException("ai.platform.read-timeout must be positive");
    }
    if (loggerLevel != null && loggerLevel.isBlank()) {
      throw new IllegalArgumentException("ai.platform.logger-level must not be blank");
    }
  }

  /**
   * Feign HTTP compression settings.
   *
   * @param requestEnabled whether request compression is enabled
   * @param responseEnabled whether response compression is enabled
   * @param minRequestSize minimum request body size in bytes before compression
   */
  public record CompressionProperties(
      boolean requestEnabled, boolean responseEnabled, @Positive int minRequestSize) {}

  /**
   * Resilience4j binding for AI Platform calls.
   *
   * @param instance shared Resilience4j instance name
   */
  public record ResilienceProperties(@NotBlank String instance) {}

  /**
   * Retry policy metadata retained for configuration compatibility.
   *
   * @param enabled whether retries are enabled at the Resilience4j layer
   * @param maxAttempts maximum attempts including the initial call
   * @param period initial backoff period
   * @param maxPeriod maximum backoff period
   */
  public record RetryProperties(
      boolean enabled,
      @Positive int maxAttempts,
      @NotNull Duration period,
      @NotNull Duration maxPeriod) {

    /**
     * Creates retry properties with invariant checks.
     *
     * @param enabled whether retries are enabled
     * @param maxAttempts maximum attempts
     * @param period initial period
     * @param maxPeriod maximum period
     */
    public RetryProperties {
      if (period != null && (period.isZero() || period.isNegative())) {
        throw new IllegalArgumentException("ai.platform.retry.period must be positive");
      }
      if (maxPeriod != null && (maxPeriod.isZero() || maxPeriod.isNegative())) {
        throw new IllegalArgumentException("ai.platform.retry.max-period must be positive");
      }
      if (period != null && maxPeriod != null && maxPeriod.compareTo(period) < 0) {
        throw new IllegalArgumentException(
            "ai.platform.retry.max-period must be greater than or equal to period");
      }
    }
  }
}
