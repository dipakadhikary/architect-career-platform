package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;

/** Raised when a recruiter email already exists for the authenticated owner. */
public class DuplicateRecruiterEmailException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a duplicate-email exception.
   *
   * @param email conflicting recruiter email
   */
  public DuplicateRecruiterEmailException(String email) {
    super(ErrorCode.BUSINESS_RULE_VIOLATION, "Recruiter with email '" + email + "' already exists");
  }
}
