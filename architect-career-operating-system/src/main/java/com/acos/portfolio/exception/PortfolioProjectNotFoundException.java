package com.acos.portfolio.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a portfolio project cannot be found for the authenticated owner. */
public class PortfolioProjectNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a project id.
   *
   * @param projectId missing project id
   */
  public PortfolioProjectNotFoundException(UUID projectId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "PortfolioProject not found with identifier '" + projectId + "'");
  }
}
