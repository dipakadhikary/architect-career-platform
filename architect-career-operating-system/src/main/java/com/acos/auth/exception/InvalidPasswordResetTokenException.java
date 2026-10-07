package com.acos.auth.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;

/** Raised when a password reset token is missing, expired, used, or malformed. */
public class InvalidPasswordResetTokenException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  private static final String MESSAGE = "This password reset link is invalid or has expired.";

  /** Creates the exception with a message that does not identify an account. */
  public InvalidPasswordResetTokenException() {
    super(ErrorCode.INVALID_TOKEN, MESSAGE);
  }
}
