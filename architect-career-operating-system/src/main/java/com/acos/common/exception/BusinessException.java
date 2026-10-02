package com.acos.common.exception;

import com.acos.common.api.ApiError;
import java.util.List;
import java.util.Objects;

/** Base unchecked exception for domain and platform business failures. */
public class BusinessException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final ErrorCode errorCode;
  private final List<ApiError.FieldErrorDetail> details;

  /**
   * Creates a business exception without field details.
   *
   * @param errorCode error code
   * @param message human-readable message
   */
  public BusinessException(ErrorCode errorCode, String message) {
    this(errorCode, message, List.of());
  }

  /**
   * Creates a business exception with optional field details.
   *
   * @param errorCode error code
   * @param message human-readable message
   * @param details field-level details
   */
  public BusinessException(
      ErrorCode errorCode, String message, List<ApiError.FieldErrorDetail> details) {
    super(message);
    this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    this.details = details == null ? List.of() : List.copyOf(details);
  }

  /**
   * Creates a business exception with a cause.
   *
   * @param errorCode error code
   * @param message human-readable message
   * @param cause underlying cause
   */
  public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    this.details = List.of();
  }

  /**
   * Returns the associated error code.
   *
   * @return error code
   */
  public ErrorCode getErrorCode() {
    return errorCode;
  }

  /**
   * Returns immutable field-level details.
   *
   * @return field details
   */
  public List<ApiError.FieldErrorDetail> getDetails() {
    return details;
  }
}
