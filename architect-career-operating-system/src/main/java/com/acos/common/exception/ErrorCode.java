package com.acos.common.exception;

import org.springframework.http.HttpStatus;

/** Canonical platform error codes mapped to HTTP statuses. */
public enum ErrorCode {
  VALIDATION_FAILED("VALIDATION_FAILED", HttpStatus.BAD_REQUEST),
  WEAK_PASSWORD("WEAK_PASSWORD", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN("INVALID_TOKEN", HttpStatus.UNAUTHORIZED),
  ACCOUNT_DISABLED("ACCOUNT_DISABLED", HttpStatus.FORBIDDEN),
  ACCESS_DENIED("ACCESS_DENIED", HttpStatus.FORBIDDEN),
  EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
  RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND),
  ROLE_NOT_FOUND("ROLE_NOT_FOUND", HttpStatus.NOT_FOUND),
  BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", HttpStatus.UNPROCESSABLE_ENTITY),
  AI_PLATFORM_ERROR("AI_PLATFORM_ERROR", HttpStatus.BAD_GATEWAY),
  AI_PLATFORM_UNAVAILABLE("AI_PLATFORM_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
  AI_TIMEOUT("AI_TIMEOUT", HttpStatus.GATEWAY_TIMEOUT),
  AI_AUTHENTICATION_FAILED("AI_AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED),
  AI_VALIDATION_FAILED("AI_VALIDATION_FAILED", HttpStatus.BAD_REQUEST),
  AI_RATE_LIMITED("AI_RATE_LIMITED", HttpStatus.TOO_MANY_REQUESTS),
  INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final HttpStatus httpStatus;

  ErrorCode(String code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  /**
   * Returns the stable machine-readable code.
   *
   * @return error code value
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the associated HTTP status.
   *
   * @return HTTP status
   */
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
