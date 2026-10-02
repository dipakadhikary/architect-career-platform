package com.acos.integration.metrics;

import com.acos.integration.config.AiPlatformProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Micrometer instrumentation for AI Platform interactions aligned with Prometheus conventions
 * ({@code acos_ai_platform_*}).
 */
@Component
public class AiPlatformMetrics {

  private static final String METRIC_REQUESTS = "acos.ai.platform.requests";
  private static final String METRIC_LATENCY = "acos.ai.platform.request.duration";
  private static final String METRIC_RETRIES = "acos.ai.platform.retries";
  private static final String METRIC_TIMEOUTS = "acos.ai.platform.timeouts";
  private static final String METRIC_CB_STATE = "acos.ai.platform.circuitbreaker.state";
  private static final String TAG_FEATURE = "feature";
  private static final String TAG_CAPABILITY = "capability";
  private static final String TAG_OUTCOME = "outcome";

  private final MeterRegistry meterRegistry;
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final String resilienceInstance;

  /**
   * Creates AI Platform metrics recorder.
   *
   * @param meterRegistry Micrometer registry
   * @param circuitBreakerRegistry Resilience4j circuit breaker registry
   * @param properties AI Platform properties
   */
  public AiPlatformMetrics(
      MeterRegistry meterRegistry,
      CircuitBreakerRegistry circuitBreakerRegistry,
      AiPlatformProperties properties) {
    this.meterRegistry = Objects.requireNonNull(meterRegistry, "meterRegistry must not be null");
    this.circuitBreakerRegistry =
        Objects.requireNonNull(circuitBreakerRegistry, "circuitBreakerRegistry must not be null");
    this.resilienceInstance = properties.resilience().instance();
  }

  /** Registers circuit-breaker state gauges. */
  @PostConstruct
  void registerCircuitBreakerGauges() {
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(resilienceInstance);
    Gauge.builder(METRIC_CB_STATE, circuitBreaker, cb -> stateOrdinal(cb.getState()))
        .description(
            "AI Platform circuit breaker state"
                + " (0=CLOSED,1=OPEN,2=HALF_OPEN,3=DISABLED,4=FORCED_OPEN,5=METRICS_ONLY)")
        .tag("instance", resilienceInstance)
        .register(meterRegistry);
  }

  /**
   * Records a successful AI Platform capability invocation.
   *
   * @param feature feature module
   * @param capability business capability name
   * @param duration call duration
   */
  public void recordSuccess(String feature, String capability, Duration duration) {
    record(feature, capability, "success", duration);
  }

  /**
   * Records a failed AI Platform capability invocation.
   *
   * @param feature feature module
   * @param capability business capability name
   * @param duration call duration
   */
  public void recordFailure(String feature, String capability, Duration duration) {
    record(feature, capability, "failure", duration);
  }

  /**
   * Records a mocked AI Platform capability invocation (feature disabled).
   *
   * @param feature feature module
   * @param capability business capability name
   * @param duration call duration
   */
  public void recordMocked(String feature, String capability, Duration duration) {
    record(feature, capability, "mocked", duration);
  }

  /**
   * Records a retry attempt for an AI Platform call.
   *
   * @param feature feature module
   * @param capability business capability name
   */
  public void recordRetry(String feature, String capability) {
    meterRegistry
        .counter(METRIC_RETRIES, TAG_FEATURE, safe(feature), TAG_CAPABILITY, safe(capability))
        .increment();
  }

  /**
   * Records a timeout for an AI Platform call.
   *
   * @param feature feature module
   * @param capability business capability name
   */
  public void recordTimeout(String feature, String capability) {
    meterRegistry
        .counter(METRIC_TIMEOUTS, TAG_FEATURE, safe(feature), TAG_CAPABILITY, safe(capability))
        .increment();
  }

  private void record(String feature, String capability, String outcome, Duration duration) {
    String safeFeature = safe(feature);
    String safeCapability = safe(capability);
    meterRegistry
        .counter(
            METRIC_REQUESTS,
            TAG_FEATURE,
            safeFeature,
            TAG_CAPABILITY,
            safeCapability,
            TAG_OUTCOME,
            outcome)
        .increment();
    Timer.builder(METRIC_LATENCY)
        .description("AI Platform request latency")
        .tag(TAG_FEATURE, safeFeature)
        .tag(TAG_CAPABILITY, safeCapability)
        .tag(TAG_OUTCOME, outcome)
        .publishPercentileHistogram()
        .register(meterRegistry)
        .record(duration == null ? Duration.ZERO : duration);
  }

  private static String safe(String value) {
    return value == null || value.isBlank() ? "unknown" : value;
  }

  private static double stateOrdinal(CircuitBreaker.State state) {
    return state.ordinal();
  }
}
