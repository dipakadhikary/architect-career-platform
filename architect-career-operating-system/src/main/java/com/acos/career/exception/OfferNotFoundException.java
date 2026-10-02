package com.acos.career.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when an offer cannot be found for the authenticated owner. */
public class OfferNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for an offer id.
   *
   * @param offerId missing offer id
   */
  public OfferNotFoundException(UUID offerId) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "Offer not found with identifier '" + offerId + "'");
  }
}
