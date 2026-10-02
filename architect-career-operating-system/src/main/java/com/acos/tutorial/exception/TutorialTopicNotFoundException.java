package com.acos.tutorial.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

public class TutorialTopicNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public TutorialTopicNotFoundException(UUID topicId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND, "TutorialTopic not found with identifier '" + topicId + "'");
  }

  public TutorialTopicNotFoundException(String path) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "TutorialTopic not found with path '" + path + "'");
  }
}
