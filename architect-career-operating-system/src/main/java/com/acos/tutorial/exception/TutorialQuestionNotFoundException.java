package com.acos.tutorial.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

public class TutorialQuestionNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public TutorialQuestionNotFoundException(UUID questionId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "TutorialQuestion not found with identifier '" + questionId + "'");
  }
}
