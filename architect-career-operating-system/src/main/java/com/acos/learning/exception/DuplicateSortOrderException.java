package com.acos.learning.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;

/** Raised when a learning sort order collides within a parent container. */
public class DuplicateSortOrderException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a duplicate sort-order exception.
   *
   * @param message human-readable message
   * @param cause underlying persistence cause
   */
  public DuplicateSortOrderException(String message, Throwable cause) {
    super(ErrorCode.BUSINESS_RULE_VIOLATION, message, cause);
  }
}
