package com.acos.portfolio.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a certification cannot be found for the authenticated owner. */
public class CertificationNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a certification id.
   *
   * @param certificationId missing certification id
   */
  public CertificationNotFoundException(UUID certificationId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "Certification not found with identifier '" + certificationId + "'");
  }
}
