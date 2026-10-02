package com.acos.integration.support;

import com.acos.common.exception.ErrorCode;
import com.acos.integration.config.AiPlatformProperties;
import com.acos.integration.exception.AiPlatformException;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.exception.AiTimeoutException;
import com.acos.integration.logging.AiPlatformCallLog;
import com.acos.integration.logging.AiPlatformCallLogger;
import com.acos.integration.metrics.AiPlatformMetrics;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/**
 * Executes AI Platform capability calls with feature toggle, Resilience4j decorators, metrics, and
 * structured logging.
 */
@Component
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidCatchingGenericException"})
public class AiPlatformInvoker {

  private final AiPlatformProperties properties;
  private final AiPlatformMetrics metrics;
  private final AiPlatformCallLogger callLogger;
  private final CircuitBreaker circuitBreaker;
  private final Retry retry;
  private final Bulkhead bulkhead;
  private final TimeLimiter timeLimiter;

  /**
   * Creates the invoker.
   *
   * @param properties AI Platform properties
   * @param metrics metrics recorder
   * @param callLogger structured call logger
   * @param circuitBreakerRegistry circuit breaker registry
   * @param retryRegistry retry registry
   * @param bulkheadRegistry bulkhead registry
   * @param timeLimiterRegistry time limiter registry
   */
  public AiPlatformInvoker(
      AiPlatformProperties properties,
      AiPlatformMetrics metrics,
      AiPlatformCallLogger callLogger,
      CircuitBreakerRegistry circuitBreakerRegistry,
      RetryRegistry retryRegistry,
      BulkheadRegistry bulkheadRegistry,
      TimeLimiterRegistry timeLimiterRegistry) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
    this.metrics = Objects.requireNonNull(metrics, "metrics must not be null");
    this.callLogger = Objects.requireNonNull(callLogger, "callLogger must not be null");
    String instance = properties.resilience().instance();
    this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(instance);
    this.retry = retryRegistry.retry(instance);
    this.bulkhead = bulkheadRegistry.bulkhead(instance);
    this.timeLimiter = timeLimiterRegistry.timeLimiter(instance);
    this.retry.getEventPublisher().onRetry(event -> metrics.recordRetry(instance, "resilience"));
  }

  /**
   * Executes a live AI call when enabled, otherwise returns the mocked supplier result.
   *
   * @param feature feature module name
   * @param capability capability name
   * @param endpoint relative endpoint
   * @param liveCall Feign-backed supplier
   * @param mockedCall mocked supplier used when AI Platform integration is disabled
   * @param <T> response type
   * @return live or mocked response
   */
  public <T> T execute(
      String feature,
      String capability,
      String endpoint,
      Supplier<T> liveCall,
      Supplier<T> mockedCall) {
    long startedNanos = System.nanoTime();
    if (!properties.enabled()) {
      T mocked = mockedCall.get();
      Duration latency = Duration.ofNanos(System.nanoTime() - startedNanos);
      metrics.recordMocked(feature, capability, latency);
      callLogger.logCall(
          new AiPlatformCallLog(feature, capability, endpoint, latency.toMillis(), "MOCKED"));
      return mocked;
    }

    try {
      T result = decorate(liveCall).get();
      Duration latency = Duration.ofNanos(System.nanoTime() - startedNanos);
      metrics.recordSuccess(feature, capability, latency);
      callLogger.logCall(
          new AiPlatformCallLog(feature, capability, endpoint, latency.toMillis(), "SUCCESS"));
      return result;
    } catch (RuntimeException ex) {
      Duration latency = Duration.ofNanos(System.nanoTime() - startedNanos);
      RuntimeException mapped = mapFailure(feature, capability, ex);
      metrics.recordFailure(feature, capability, latency);
      callLogger.logCall(
          new AiPlatformCallLog(feature, capability, endpoint, latency.toMillis(), "FAILURE"));
      throw mapped;
    }
  }

  private <T> Supplier<T> decorate(Supplier<T> liveCall) {
    Supplier<T> timed = () -> invokeWithTimeLimit(liveCall);
    Supplier<T> withBulkhead = Bulkhead.decorateSupplier(bulkhead, timed);
    Supplier<T> withCircuitBreaker = CircuitBreaker.decorateSupplier(circuitBreaker, withBulkhead);
    return Retry.decorateSupplier(retry, withCircuitBreaker);
  }

  private <T> T invokeWithTimeLimit(Supplier<T> liveCall) {
    Duration timeout = timeLimiter.getTimeLimiterConfig().getTimeoutDuration();
    try {
      return CompletableFuture.supplyAsync(liveCall)
          .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
          .join();
    } catch (CompletionException ex) {
      throw launderCompletion(ex);
    }
  }

  private RuntimeException mapFailure(String feature, String capability, Throwable ex) {
    Throwable root = unwrap(ex);
    if (root instanceof CallNotPermittedException) {
      return new AiPlatformUnavailableException(
          "AI Platform circuit breaker open for " + feature + "/" + capability, root);
    }
    if (root instanceof BulkheadFullException) {
      return new AiPlatformUnavailableException(
          "AI Platform bulkhead full for " + feature + "/" + capability, root);
    }
    if (root instanceof TimeoutException) {
      metrics.recordTimeout(feature, capability);
      return new AiTimeoutException(
          "AI Platform call timed out for " + feature + "/" + capability, root);
    }
    if (root instanceof AiPlatformException aiPlatformException) {
      return aiPlatformException;
    }
    if (root instanceof RuntimeException runtimeException) {
      return runtimeException;
    }
    return new AiPlatformException(
        ErrorCode.AI_PLATFORM_ERROR,
        "AI Platform call failed for " + feature + "/" + capability,
        root);
  }

  private static RuntimeException launderCompletion(CompletionException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof RuntimeException runtimeException) {
      return runtimeException;
    }
    if (cause instanceof TimeoutException timeoutException) {
      return new CompletionException(timeoutException);
    }
    if (cause != null) {
      return new CompletionException(cause);
    }
    return ex;
  }

  private static Throwable unwrap(Throwable ex) {
    Throwable current = ex;
    while (current instanceof CompletionException || current instanceof ExecutionException) {
      Throwable cause = current.getCause();
      if (cause == null || Objects.equals(cause, current)) {
        break;
      }
      current = cause;
    }
    return current;
  }
}
