package com.acos.auth.exception;

import com.acos.common.api.ApiError;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;
import java.util.List;

/** Raised when a password fails platform password policy checks. */
public class WeakPasswordException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates a weak-password exception without field details.
   *
   * @param message validation summary
   */
  public WeakPasswordException(String message) {
    super(ErrorCode.WEAK_PASSWORD, message);
  }

  /**
   * Creates a weak-password exception with field details.
   *
   * @param message validation summary
   * @param details field-level details
   */
  public WeakPasswordException(String message, List<ApiError.FieldErrorDetail> details) {
    super(ErrorCode.WEAK_PASSWORD, message, details);
  }
}
