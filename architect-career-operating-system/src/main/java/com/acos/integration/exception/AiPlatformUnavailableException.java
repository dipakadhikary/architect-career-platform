package com.acos.integration.exception;

import com.acos.common.exception.ErrorCode;

/** Raised when the AI Platform is unavailable or returns a gateway/service-unavailable error. */
public class AiPlatformUnavailableException extends AiPlatformException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an unavailable exception.
   *
   * @param message human-readable message
   */
  public AiPlatformUnavailableException(String message) {
    super(ErrorCode.AI_PLATFORM_UNAVAILABLE, message);
  }

  /**
   * Creates an unavailable exception with a cause.
   *
   * @param message human-readable message
   * @param cause underlying cause
   */
  public AiPlatformUnavailableException(String message, Throwable cause) {
    super(ErrorCode.AI_PLATFORM_UNAVAILABLE, message, cause);
  }
}
