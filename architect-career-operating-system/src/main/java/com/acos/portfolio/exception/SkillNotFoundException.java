package com.acos.portfolio.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a skill cannot be found for the authenticated owner. */
public class SkillNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a skill id.
   *
   * @param skillId missing skill id
   */
  public SkillNotFoundException(UUID skillId) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "Skill not found with identifier '" + skillId + "'");
  }
}
