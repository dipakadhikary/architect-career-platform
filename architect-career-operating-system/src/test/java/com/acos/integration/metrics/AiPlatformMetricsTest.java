package com.acos.integration.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.integration.config.AiPlatformProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link AiPlatformMetrics}. */
class AiPlatformMetricsTest {

  private MeterRegistry meterRegistry;
  private AiPlatformMetrics metrics;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    AiPlatformProperties properties =
        new AiPlatformProperties(
            false,
            "http://localhost:8090",
            "key",
            Duration.ofSeconds(3),
            Duration.ofSeconds(30),
            "BASIC",
            new AiPlatformProperties.CompressionProperties(true, true, 2048),
            new AiPlatformProperties.ResilienceProperties("ai-platform"),
            new AiPlatformProperties.RetryProperties(
                true, 3, Duration.ofMillis(200), Duration.ofSeconds(2)));
    metrics = new AiPlatformMetrics(meterRegistry, CircuitBreakerRegistry.ofDefaults(), properties);
    metrics.registerCircuitBreakerGauges();
  }

  @Test
  void shouldRecordSuccessFailureRetryAndTimeout() {
    metrics.recordSuccess("knowledge", "index", Duration.ofMillis(12));
    metrics.recordFailure("knowledge", "index", Duration.ofMillis(20));
    metrics.recordRetry("knowledge", "index");
    metrics.recordTimeout("knowledge", "index");

    assertThat(
            meterRegistry
                .counter(
                    "acos.ai.platform.requests",
                    "feature",
                    "knowledge",
                    "capability",
                    "index",
                    "outcome",
                    "success")
                .count())
        .isEqualTo(1.0d);
    assertThat(
            meterRegistry
                .counter(
                    "acos.ai.platform.requests",
                    "feature",
                    "knowledge",
                    "capability",
                    "index",
                    "outcome",
                    "failure")
                .count())
        .isEqualTo(1.0d);
    assertThat(
            meterRegistry
                .counter("acos.ai.platform.retries", "feature", "knowledge", "capability", "index")
                .count())
        .isEqualTo(1.0d);
    assertThat(
            meterRegistry
                .counter("acos.ai.platform.timeouts", "feature", "knowledge", "capability", "index")
                .count())
        .isEqualTo(1.0d);
    assertThat(meterRegistry.find("acos.ai.platform.circuitbreaker.state").gauge()).isNotNull();
  }
}
