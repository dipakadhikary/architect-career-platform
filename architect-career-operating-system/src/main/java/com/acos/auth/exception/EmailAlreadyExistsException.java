package com.acos.auth.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;

/** Raised when registration is attempted with an email that already exists. */
public class EmailAlreadyExistsException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates an exception for a conflicting email address.
   *
   * @param email conflicting email
   */
  public EmailAlreadyExistsException(String email) {
    super(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already registered: " + email);
  }
}
