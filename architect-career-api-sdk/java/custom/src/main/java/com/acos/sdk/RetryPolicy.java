package com.acos.sdk;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Retry policy used by custom SDK wrappers.
 */
public record RetryPolicy(
    int retries,
    long delayMs,
    double backoffFactor,
    Set<Integer> retryOnStatuses,
    BiPredicate<RuntimeException, Integer> shouldRetryPredicate) {

  public RetryPolicy {
    if (retries < 1) {
      throw new IllegalArgumentException("retries must be >= 1");
    }
    if (delayMs < 0) {
      throw new IllegalArgumentException("delayMs must be >= 0");
    }
    if (backoffFactor < 1.0d) {
      throw new IllegalArgumentException("backoffFactor must be >= 1.0");
    }
    retryOnStatuses = Set.copyOf(Objects.requireNonNull(retryOnStatuses));
    shouldRetryPredicate = Objects.requireNonNull(shouldRetryPredicate);
  }

  public static RetryPolicy defaults() {
    return new RetryPolicy(
        3,
        200L,
        2.0d,
        Set.of(408, 429, 500, 502, 503, 504),
        (ex, attempt) -> false);
  }

  public boolean shouldRetry(RuntimeException ex, int attempt) {
    if (shouldRetryPredicate.test(ex, attempt)) {
      return true;
    }
    if (ex instanceof HttpStatusCodeException http) {
      return retryOnStatuses.contains(http.getStatusCode().value());
    }
    return false;
  }
}
