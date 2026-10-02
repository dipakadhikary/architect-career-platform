package com.acos.portfolio.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when an achievement cannot be found for the authenticated owner. */
public class AchievementNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for an achievement id.
   *
   * @param achievementId missing achievement id
   */
  public AchievementNotFoundException(UUID achievementId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "Achievement not found with identifier '" + achievementId + "'");
  }
}
