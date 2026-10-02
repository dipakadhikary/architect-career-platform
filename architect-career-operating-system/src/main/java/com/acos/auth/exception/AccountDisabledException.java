package com.acos.auth.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;

/** Raised when authentication succeeds but the account is disabled. */
public class AccountDisabledException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  private static final String MESSAGE = "Account is disabled";

  /** Creates an account-disabled exception. */
  public AccountDisabledException() {
    super(ErrorCode.ACCOUNT_DISABLED, MESSAGE);
  }
}
