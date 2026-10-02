package com.acos.learning.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a learning plan cannot be found for the authenticated owner. */
public class LearningPlanNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a plan id.
   *
   * @param planId missing plan id
   */
  public LearningPlanNotFoundException(UUID planId) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "LearningPlan not found with identifier '" + planId + "'");
  }
}
