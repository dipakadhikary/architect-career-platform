package com.acos.integration.facade;

import com.acos.common.logging.CorrelationIdFilter;
import com.acos.integration.config.AiPlatformProperties;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Shared facade execution helper: feature toggle, logging, and graceful fallback on AI failures.
 */
@Component
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class AiFacadeSupport {

  private static final Logger LOG = LoggerFactory.getLogger(AiFacadeSupport.class);

  private final AiPlatformProperties properties;

  /**
   * Creates the facade support helper.
   *
   * @param properties AI Platform properties
   */
  public AiFacadeSupport(AiPlatformProperties properties) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
  }

  /**
   * Returns whether AI Platform calls are enabled.
   *
   * @return {@code true} when enabled
   */
  public boolean isEnabled() {
    return properties.enabled();
  }

  /**
   * Executes a live AI call when enabled; otherwise returns the fallback. Live failures never
   * propagate to callers—fallback is returned after optional failure handling.
   *
   * @param feature feature name
   * @param capability capability name
   * @param liveCall Feign-backed call through the gateway
   * @param fallbackSupplier fallback response supplier
   * @param onFailure optional failure callback receiving the cause
   * @param <T> response type
   * @return live or fallback response
   */
  public <T> T execute(
      String feature,
      String capability,
      Supplier<T> liveCall,
      Supplier<T> fallbackSupplier,
      Consumer<Throwable> onFailure) {
    Objects.requireNonNull(liveCall, "liveCall must not be null");
    Objects.requireNonNull(fallbackSupplier, "fallbackSupplier must not be null");

    if (!properties.enabled()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(
            "AI facade fallback correlationId={} feature={} capability={} reason=disabled",
            correlationId(),
            feature,
            capability);
      }
      return fallbackSupplier.get();
    }

    long started = System.nanoTime();
    try {
      T result = liveCall.get();
      if (LOG.isDebugEnabled()) {
        LOG.debug(
            "AI facade succeeded correlationId={} feature={} capability={} latencyMs={}",
            correlationId(),
            feature,
            capability,
            Duration.ofNanos(System.nanoTime() - started).toMillis());
      }
      return result;
    } catch (RuntimeException ex) {
      if (LOG.isWarnEnabled()) {
        LOG.warn(
            "AI facade fallback correlationId={} feature={} capability={} latencyMs={}"
                + " reason={} message={}",
            correlationId(),
            feature,
            capability,
            Duration.ofNanos(System.nanoTime() - started).toMillis(),
            ex.getClass().getSimpleName(),
            ex.getMessage());
      }
      if (onFailure != null) {
        onFailure.accept(ex);
      }
      return fallbackSupplier.get();
    }
  }

  private static String correlationId() {
    String value = MDC.get(CorrelationIdFilter.MDC_KEY);
    return value == null || value.isBlank() ? "-" : value;
  }
}
