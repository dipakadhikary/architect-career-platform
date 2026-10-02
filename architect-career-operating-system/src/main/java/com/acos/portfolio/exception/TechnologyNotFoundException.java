package com.acos.portfolio.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a technology cannot be found for the authenticated owner. */
public class TechnologyNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a technology id.
   *
   * @param technologyId missing technology id
   */
  public TechnologyNotFoundException(UUID technologyId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "Technology not found with identifier '" + technologyId + "'");
  }
}
