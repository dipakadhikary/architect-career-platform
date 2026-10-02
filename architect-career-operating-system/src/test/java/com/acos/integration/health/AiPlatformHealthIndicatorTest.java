package com.acos.integration.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.acos.integration.config.AiPlatformProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/** Unit tests for AI Platform health indicator and service. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AiPlatformHealthIndicatorTest {

  @Mock private AiPlatformProperties properties;

  private CircuitBreakerRegistry circuitBreakerRegistry;
  private AiPlatformHealthIndicator indicator;
  private AiPlatformHealthService healthService;

  @BeforeEach
  void setUp() {
    circuitBreakerRegistry = CircuitBreakerRegistry.of(CircuitBreakerConfig.ofDefaults());
    when(properties.resilience())
        .thenReturn(new AiPlatformProperties.ResilienceProperties("ai-platform"));
    indicator = new AiPlatformHealthIndicator(properties, circuitBreakerRegistry);
    healthService =
        new AiPlatformHealthService(
            properties,
            circuitBreakerRegistry,
            Clock.fixed(Instant.parse("2026-08-05T10:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  void shouldReportUnknownWhenDisabled() {
    when(properties.enabled()).thenReturn(false);

    Health health = indicator.health();

    assertThat(health.getStatus().getCode()).isEqualTo("UNKNOWN");
    assertThat(healthService.checkHealth().status()).isEqualTo(AiPlatformHealthStatus.UNAVAILABLE);
  }

  @Test
  void shouldReportUpWhenEnabledAndClosed() {
    when(properties.enabled()).thenReturn(true);
    when(properties.baseUrl()).thenReturn("http://localhost:8090");
    circuitBreakerRegistry.circuitBreaker("ai-platform");

    Health health = indicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.UP);
    assertThat(healthService.checkHealth().status()).isEqualTo(AiPlatformHealthStatus.AVAILABLE);
  }

  @Test
  void shouldReportDownWhenCircuitOpen() {
    when(properties.enabled()).thenReturn(true);
    CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("ai-platform");
    breaker.transitionToOpenState();

    Health health = indicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    assertThat(healthService.checkHealth().status()).isEqualTo(AiPlatformHealthStatus.UNAVAILABLE);
  }

  @Test
  void shouldReportDegradedWhenHalfOpen() {
    when(properties.enabled()).thenReturn(true);
    CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("ai-platform");
    breaker.transitionToOpenState();
    breaker.transitionToHalfOpenState();

    Health health = indicator.health();

    assertThat(health.getStatus().getCode()).isEqualTo("DEGRADED");
    assertThat(healthService.checkHealth().status()).isEqualTo(AiPlatformHealthStatus.DEGRADED);
  }
}
