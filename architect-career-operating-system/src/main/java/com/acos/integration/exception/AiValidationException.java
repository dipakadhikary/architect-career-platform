package com.acos.integration.exception;

import com.acos.common.exception.ErrorCode;

/** Raised when the AI Platform rejects a request due to validation errors. */
public class AiValidationException extends AiPlatformException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a validation exception.
   *
   * @param message human-readable message
   */
  public AiValidationException(String message) {
    super(ErrorCode.AI_VALIDATION_FAILED, message);
  }

  /**
   * Creates a validation exception with a cause.
   *
   * @param message human-readable message
   * @param cause underlying cause
   */
  public AiValidationException(String message, Throwable cause) {
    super(ErrorCode.AI_VALIDATION_FAILED, message, cause);
  }
}
