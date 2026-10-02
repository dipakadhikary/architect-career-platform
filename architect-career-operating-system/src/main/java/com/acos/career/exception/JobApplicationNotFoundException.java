package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a job application cannot be found for the authenticated owner. */
public class JobApplicationNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a job application id.
   *
   * @param applicationId missing application id
   */
  public JobApplicationNotFoundException(UUID applicationId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "JobApplication not found with identifier '" + applicationId + "'");
  }
}
