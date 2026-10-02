package com.acos.integration.exception;

import com.acos.common.exception.ErrorCode;

/** Raised when the AI Platform rate-limits the caller. */
public class AiRateLimitException extends AiPlatformException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a rate-limit exception.
   *
   * @param message human-readable message
   */
  public AiRateLimitException(String message) {
    super(ErrorCode.AI_RATE_LIMITED, message);
  }

  /**
   * Creates a rate-limit exception with a cause.
   *
   * @param message human-readable message
   * @param cause underlying cause
   */
  public AiRateLimitException(String message, Throwable cause) {
    super(ErrorCode.AI_RATE_LIMITED, message, cause);
  }
}
