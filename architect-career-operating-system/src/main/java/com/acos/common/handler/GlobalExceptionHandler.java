package com.acos.common.handler;

import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Centralized REST exception translation into {@link ApiResponse} envelopes. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles platform business exceptions.
   *
   * @param exception business exception
   * @return mapped API response
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    if (LOG.isWarnEnabled()) {
      LOG.warn("Business exception [{}]: {}", errorCode.getCode(), exception.getMessage());
    }

    ApiError error =
        ApiError.create(errorCode.getCode(), exception.getMessage(), exception.getDetails());
    return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(error));
  }

  /**
   * Handles Bean Validation failures on request bodies.
   *
   * @param exception validation exception
   * @return mapped API response
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception) {
    List<ApiError.FieldErrorDetail> details =
        exception.getBindingResult().getFieldErrors().stream().map(this::toFieldError).toList();

    ApiError error =
        ApiError.create(
            ErrorCode.VALIDATION_FAILED.getCode(), "Request validation failed", details);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(error));
  }

  /**
   * Handles constraint violations outside request-body binding.
   *
   * @param exception constraint violation exception
   * @return mapped API response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
      ConstraintViolationException exception) {
    List<ApiError.FieldErrorDetail> details =
        exception.getConstraintViolations().stream()
            .map(
                violation ->
                    new ApiError.FieldErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
            .toList();

    ApiError error =
        ApiError.create(
            ErrorCode.VALIDATION_FAILED.getCode(), "Constraint validation failed", details);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(error));
  }

  /**
   * Handles malformed request payloads.
   *
   * @param exception message conversion exception
   * @return mapped API response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception) {
    if (LOG.isWarnEnabled()) {
      LOG.warn("Malformed request body: {}", exception.getMessage());
    }
    ApiError error =
        ApiError.create(ErrorCode.VALIDATION_FAILED.getCode(), "Malformed request body");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(error));
  }

  /**
   * Handles argument type mismatches.
   *
   * @param exception type mismatch exception
   * @return mapped API response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException exception) {
    String message =
        "Parameter '%s' has invalid value '%s'"
            .formatted(exception.getName(), exception.getValue());
    ApiError error = ApiError.create(ErrorCode.VALIDATION_FAILED.getCode(), message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(error));
  }

  /**
   * Handles missing static or API resources.
   *
   * @param exception missing resource exception
   * @return mapped API response
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
      NoResourceFoundException exception) {
    ApiError error =
        ApiError.create(ErrorCode.RESOURCE_NOT_FOUND.getCode(), exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(error));
  }

  /**
   * Handles unexpected failures.
   *
   * @param exception unexpected exception
   * @return mapped API response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
    LOG.error("Unhandled exception", exception);
    ApiError error =
        ApiError.create(ErrorCode.INTERNAL_ERROR.getCode(), "An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(error));
  }

  private ApiError.FieldErrorDetail toFieldError(FieldError fieldError) {
    return new ApiError.FieldErrorDetail(
        fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue());
  }
}
