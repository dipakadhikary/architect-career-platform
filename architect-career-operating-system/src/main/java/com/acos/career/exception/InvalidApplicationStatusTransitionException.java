package com.acos.career.exception;

import com.acos.career.entity.ApplicationStatus;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;

/** Raised when a requested application status transition is not allowed. */
public class InvalidApplicationStatusTransitionException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an invalid-transition exception for the given status pair.
   *
   * @param currentStatus current application status
   * @param targetStatus requested target status
   */
  public InvalidApplicationStatusTransitionException(
      ApplicationStatus currentStatus, ApplicationStatus targetStatus) {
    super(
        ErrorCode.BUSINESS_RULE_VIOLATION,
        "Cannot transition application status from '"
            + currentStatus
            + "' to '"
            + targetStatus
            + "'");
  }
}
