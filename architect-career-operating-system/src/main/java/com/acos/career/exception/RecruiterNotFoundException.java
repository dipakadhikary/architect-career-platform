package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a recruiter cannot be found for the authenticated owner. */
public class RecruiterNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a recruiter id.
   *
   * @param recruiterId missing recruiter id
   */
  public RecruiterNotFoundException(UUID recruiterId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND, "Recruiter not found with identifier '" + recruiterId + "'");
  }
}
