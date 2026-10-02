package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;

/** Raised when a company name already exists for the authenticated owner. */
public class DuplicateCompanyNameException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a duplicate-name exception.
   *
   * @param name conflicting company name
   */
  public DuplicateCompanyNameException(String name) {
    super(ErrorCode.BUSINESS_RULE_VIOLATION, "Company with name '" + name + "' already exists");
  }
}
