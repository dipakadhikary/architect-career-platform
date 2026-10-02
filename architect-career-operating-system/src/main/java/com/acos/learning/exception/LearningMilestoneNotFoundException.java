package com.acos.learning.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a learning milestone cannot be found for the authenticated owner. */
public class LearningMilestoneNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a milestone id.
   *
   * @param milestoneId missing milestone id
   */
  public LearningMilestoneNotFoundException(UUID milestoneId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "LearningMilestone not found with identifier '" + milestoneId + "'");
  }
}
