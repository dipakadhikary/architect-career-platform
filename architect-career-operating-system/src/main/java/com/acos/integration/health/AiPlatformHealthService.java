package com.acos.integration.health;

import com.acos.integration.config.AiPlatformProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Resolves AI Platform health for the Business Platform REST API, aligning with Actuator health
 * semantics.
 */
@Service
public class AiPlatformHealthService {

  private final AiPlatformProperties properties;
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final Clock clock;

  /**
   * Creates the health service.
   *
   * @param properties AI Platform properties
   * @param circuitBreakerRegistry circuit breaker registry
   * @param clock clock used for checked-at timestamps
   */
  public AiPlatformHealthService(
      AiPlatformProperties properties, CircuitBreakerRegistry circuitBreakerRegistry, Clock clock) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
    this.circuitBreakerRegistry =
        Objects.requireNonNull(circuitBreakerRegistry, "circuitBreakerRegistry must not be null");
    this.clock = Objects.requireNonNull(clock, "clock must not be null");
  }

  /**
   * Returns the current AI Platform health status.
   *
   * @return health response
   */
  public AiPlatformHealthResponse checkHealth() {
    if (!properties.enabled()) {
      return new AiPlatformHealthResponse(
          AiPlatformHealthStatus.UNAVAILABLE,
          "AI Platform integration is disabled; capability gateways return mocked responses",
          Instant.now(clock),
          false);
    }

    CircuitBreaker.State state =
        circuitBreakerRegistry.circuitBreaker(properties.resilience().instance()).getState();
    return switch (state) {
      case OPEN, FORCED_OPEN ->
          new AiPlatformHealthResponse(
              AiPlatformHealthStatus.UNAVAILABLE,
              "AI Platform circuit breaker is open",
              Instant.now(clock),
              true);
      case HALF_OPEN ->
          new AiPlatformHealthResponse(
              AiPlatformHealthStatus.DEGRADED,
              "AI Platform circuit breaker is half-open",
              Instant.now(clock),
              true);
      case CLOSED, DISABLED, METRICS_ONLY ->
          new AiPlatformHealthResponse(
              AiPlatformHealthStatus.AVAILABLE,
              "AI Platform integration is available",
              Instant.now(clock),
              true);
    };
  }
}
