package com.acos.integration.exception;

import com.acos.common.exception.ErrorCode;

/** Raised when the AI Platform rejects authentication or authorization. */
public class AiAuthenticationException extends AiPlatformException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an authentication exception.
   *
   * @param message human-readable message
   */
  public AiAuthenticationException(String message) {
    super(ErrorCode.AI_AUTHENTICATION_FAILED, message);
  }

  /**
   * Creates an authentication exception with a cause.
   *
   * @param message human-readable message
   * @param cause underlying cause
   */
  public AiAuthenticationException(String message, Throwable cause) {
    super(ErrorCode.AI_AUTHENTICATION_FAILED, message, cause);
  }
}
