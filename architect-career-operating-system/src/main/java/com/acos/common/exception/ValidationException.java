package com.acos.common.exception;

import com.acos.common.api.ApiError;
import java.util.List;

/** Exception raised when request input fails validation rules. */
public class ValidationException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a validation exception without field details.
   *
   * @param message validation summary
   */
  public ValidationException(String message) {
    super(ErrorCode.VALIDATION_FAILED, message);
  }

  /**
   * Creates a validation exception with field details.
   *
   * @param message validation summary
   * @param details field-level validation details
   */
  public ValidationException(String message, List<ApiError.FieldErrorDetail> details) {
    super(ErrorCode.VALIDATION_FAILED, message, details);
  }
}
