package com.acos.common.api;

import java.util.List;
import java.util.Objects;

/**
 * Standardized API error payload returned to clients.
 *
 * @param code machine-readable error code
 * @param message human-readable error summary
 * @param details optional field-level validation details
 */
public record ApiError(String code, String message, List<FieldErrorDetail> details) {

  /**
   * Field-level validation detail.
   *
   * @param field field or property name
   * @param message validation failure message
   * @param rejectedValue rejected input value, when available
   */
  public record FieldErrorDetail(String field, String message, Object rejectedValue) {

    /**
     * Creates a field error without a rejected value.
     *
     * @param field field name
     * @param message validation message
     * @return field error detail
     */
    public static FieldErrorDetail ofField(String field, String message) {
      return new FieldErrorDetail(field, message, null);
    }
  }

  /**
   * Creates an immutable API error.
   *
   * @param code error code
   * @param message error message
   * @param details field details, nullable
   */
  public ApiError(String code, String message, List<FieldErrorDetail> details) {
    this.code = Objects.requireNonNull(code, "code must not be null");
    this.message = Objects.requireNonNull(message, "message must not be null");
    this.details = details == null ? List.of() : List.copyOf(details);
  }

  /**
   * Creates an error without field details.
   *
   * @param code error code
   * @param message error message
   * @return API error
   */
  public static ApiError create(String code, String message) {
    return new ApiError(code, message, List.of());
  }

  /**
   * Creates an error with field details.
   *
   * @param code error code
   * @param message error message
   * @param details field details
   * @return API error
   */
  public static ApiError create(String code, String message, List<FieldErrorDetail> details) {
    return new ApiError(code, message, details);
  }
}
