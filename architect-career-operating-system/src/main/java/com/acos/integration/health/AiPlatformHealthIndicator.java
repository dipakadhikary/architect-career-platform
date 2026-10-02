package com.acos.integration.health;

import com.acos.integration.config.AiPlatformProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.Objects;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Actuator health indicator for the AI Platform integration. Reports UP, DOWN, DEGRADED, or UNKNOWN
 * without requiring the Python AI Platform to be online during foundation operation.
 */
@Component("aiPlatform")
public class AiPlatformHealthIndicator implements HealthIndicator {

  private final AiPlatformProperties properties;
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  /**
   * Creates the health indicator.
   *
   * @param properties AI Platform properties
   * @param circuitBreakerRegistry circuit breaker registry
   */
  public AiPlatformHealthIndicator(
      AiPlatformProperties properties, CircuitBreakerRegistry circuitBreakerRegistry) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
    this.circuitBreakerRegistry =
        Objects.requireNonNull(circuitBreakerRegistry, "circuitBreakerRegistry must not be null");
  }

  @Override
  public Health health() {
    if (!properties.enabled()) {
      return Health.status("UNKNOWN")
          .withDetail("enabled", false)
          .withDetail("message", "AI Platform integration is disabled")
          .build();
    }

    CircuitBreaker.State state =
        circuitBreakerRegistry.circuitBreaker(properties.resilience().instance()).getState();
    return switch (state) {
      case OPEN, FORCED_OPEN ->
          Health.down()
              .withDetail("enabled", true)
              .withDetail("circuitBreakerState", state.name())
              .withDetail("status", "DOWN")
              .withDetail("message", "AI Platform circuit breaker is open")
              .build();
      case HALF_OPEN ->
          Health.status("DEGRADED")
              .withDetail("enabled", true)
              .withDetail("circuitBreakerState", state.name())
              .withDetail("status", "DEGRADED")
              .withDetail("message", "AI Platform circuit breaker is half-open")
              .build();
      case CLOSED, DISABLED, METRICS_ONLY ->
          Health.up()
              .withDetail("enabled", true)
              .withDetail("circuitBreakerState", state.name())
              .withDetail("status", "UP")
              .withDetail("message", "AI Platform integration is available")
              .withDetail("baseUrl", properties.baseUrl())
              .build();
    };
  }
}
