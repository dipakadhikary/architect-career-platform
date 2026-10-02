package com.acos.auth.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;

/** Raised when login credentials are invalid or the account cannot be resolved. */
public class InvalidCredentialsException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  private static final String MESSAGE = "Invalid email or password";

  /** Creates an invalid-credentials exception with a generic message. */
  public InvalidCredentialsException() {
    super(ErrorCode.INVALID_CREDENTIALS, MESSAGE);
  }
}
