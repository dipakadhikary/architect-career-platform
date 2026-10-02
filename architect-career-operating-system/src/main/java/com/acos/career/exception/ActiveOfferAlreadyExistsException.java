package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a job application already has an active pending offer. */
public class ActiveOfferAlreadyExistsException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an active-offer conflict exception.
   *
   * @param applicationId application that already has an active pending offer
   */
  public ActiveOfferAlreadyExistsException(UUID applicationId) {
    super(
        ErrorCode.BUSINESS_RULE_VIOLATION,
        "Job application '" + applicationId + "' already has an active pending offer");
  }
}
