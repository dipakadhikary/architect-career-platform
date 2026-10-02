package com.acos.portfolio.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;

/** Raised when a skill or technology name already exists for the authenticated owner. */
public class DuplicatePortfolioNameException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a duplicate-name exception.
   *
   * @param resourceType human-readable resource type (for example {@code Skill} or {@code
   *     Technology})
   * @param name conflicting name
   */
  public DuplicatePortfolioNameException(String resourceType, String name) {
    super(
        ErrorCode.BUSINESS_RULE_VIOLATION,
        resourceType + " with name '" + name + "' already exists");
  }
}
