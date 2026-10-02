package com.acos.integration.exception;

import com.acos.common.exception.ErrorCode;

/** Raised when an AI Platform call exceeds the configured timeout. */
public class AiTimeoutException extends AiPlatformException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a timeout exception.
   *
   * @param message human-readable message
   */
  public AiTimeoutException(String message) {
    super(ErrorCode.AI_TIMEOUT, message);
  }

  /**
   * Creates a timeout exception with a cause.
   *
   * @param message human-readable message
   * @param cause underlying cause
   */
  public AiTimeoutException(String message, Throwable cause) {
    super(ErrorCode.AI_TIMEOUT, message, cause);
  }
}
