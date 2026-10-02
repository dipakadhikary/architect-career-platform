package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when an interview cannot be found for the authenticated owner. */
public class InterviewNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for an interview id.
   *
   * @param interviewId missing interview id
   */
  public InterviewNotFoundException(UUID interviewId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND, "Interview not found with identifier '" + interviewId + "'");
  }
}
