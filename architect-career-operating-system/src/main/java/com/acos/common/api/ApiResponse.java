package com.acos.common.api;

import com.acos.common.logging.CorrelationIdFilter;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.MDC;

/**
 * Uniform API response envelope for success and failure outcomes.
 *
 * @param <T> response payload type
 * @param success whether the request completed successfully
 * @param data success payload, null on failure
 * @param error failure details, null on success
 * @param correlationId request correlation identifier
 * @param timestamp response creation timestamp
 */
public record ApiResponse<T>(
    boolean success, T data, ApiError error, String correlationId, Instant timestamp) {

  /**
   * Builds a successful response.
   *
   * @param <T> payload type
   * @param data payload
   * @return success response
   */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, data, null, currentCorrelationId(), Instant.now());
  }

  /**
   * Builds a failure response.
   *
   * @param <T> payload type
   * @param error error payload
   * @return failure response
   */
  public static <T> ApiResponse<T> failure(ApiError error) {
    Objects.requireNonNull(error, "error must not be null");
    return new ApiResponse<>(false, null, error, currentCorrelationId(), Instant.now());
  }

  private static String currentCorrelationId() {
    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
    return correlationId == null ? "" : correlationId;
  }
}
