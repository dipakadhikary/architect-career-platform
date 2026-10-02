package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when attempting to mutate a job application that has been archived. */
public class ApplicationArchivedException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception for an archived application id.
   *
   * @param applicationId archived application id
   */
  public ApplicationArchivedException(UUID applicationId) {
    super(
        ErrorCode.BUSINESS_RULE_VIOLATION,
        "JobApplication '" + applicationId + "' is archived and cannot be modified");
  }
}
