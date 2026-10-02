package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a company cannot be found for the authenticated owner. */
public class CompanyNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a company id.
   *
   * @param companyId missing company id
   */
  public CompanyNotFoundException(UUID companyId) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "Company not found with identifier '" + companyId + "'");
  }
}
