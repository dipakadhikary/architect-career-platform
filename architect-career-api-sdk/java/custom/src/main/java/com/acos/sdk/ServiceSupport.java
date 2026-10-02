package com.acos.sdk;

import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared retry and logging helpers for domain service wrappers.
 * Generated API classes remain untouched.
 */
public abstract class ServiceSupport {

  private final RetryPolicy retryPolicy;
  private final Logger logger;

  protected ServiceSupport(RetryPolicy retryPolicy, Class<?> owner) {
    this.retryPolicy = Objects.requireNonNullElseGet(retryPolicy, RetryPolicy::defaults);
    this.logger = LoggerFactory.getLogger(owner);
  }

  protected final <T> T execute(String operationName, Supplier<T> operation) {
    logger.debug("Calling {}", operationName);
    int attempt = 0;
    long delayMs = retryPolicy.delayMs();
    RuntimeException last = null;

    while (attempt < retryPolicy.retries()) {
      attempt++;
      try {
        T result = operation.get();
        logger.debug("Completed {}", operationName);
        return result;
      } catch (RuntimeException ex) {
        last = ex;
        if (attempt >= retryPolicy.retries() || !retryPolicy.shouldRetry(ex, attempt)) {
          logger.error("Failed {}", operationName, ex);
          throw ex;
        }
        logger.warn(
            "Retrying {} after attempt {}/{}: {}",
            operationName,
            attempt,
            retryPolicy.retries(),
            ex.getMessage());
        sleep(delayMs);
        delayMs = (long) (delayMs * retryPolicy.backoffFactor());
      }
    }

    throw last != null ? last : new IllegalStateException("Retry loop exited unexpectedly");
  }

  private static void sleep(long delayMs) {
    try {
      Thread.sleep(delayMs);
    } catch (InterruptedException interrupted) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Retry interrupted", interrupted);
    }
  }
}
