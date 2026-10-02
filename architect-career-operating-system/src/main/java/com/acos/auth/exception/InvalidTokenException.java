package com.acos.auth.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;

/** Raised when a presented access or refresh token is invalid or unusable. */
public class InvalidTokenException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  private static final String MESSAGE = "Invalid or expired token";

  /** Creates an invalid-token exception with a generic message. */
  public InvalidTokenException() {
    super(ErrorCode.INVALID_TOKEN, MESSAGE);
  }
}
