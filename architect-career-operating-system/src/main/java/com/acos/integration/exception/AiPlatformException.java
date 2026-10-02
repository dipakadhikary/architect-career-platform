package com.acos.integration.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;

/** Base exception for AI Platform integration failures. */
public class AiPlatformException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an AI Platform exception with the generic platform error code.
   *
   * @param message human-readable message
   */
  public AiPlatformException(String message) {
    super(ErrorCode.AI_PLATFORM_ERROR, message);
  }

  /**
   * Creates an AI Platform exception with an explicit error code.
   *
   * @param errorCode error code
   * @param message human-readable message
   */
  public AiPlatformException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  /**
   * Creates an AI Platform exception with a cause.
   *
   * @param errorCode error code
   * @param message human-readable message
   * @param cause underlying cause
   */
  public AiPlatformException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
