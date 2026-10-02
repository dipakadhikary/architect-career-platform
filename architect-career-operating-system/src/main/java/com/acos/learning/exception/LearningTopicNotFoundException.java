package com.acos.learning.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a learning topic cannot be found for the authenticated owner. */
public class LearningTopicNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a topic id.
   *
   * @param topicId missing topic id
   */
  public LearningTopicNotFoundException(UUID topicId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND, "LearningTopic not found with identifier '" + topicId + "'");
  }
}
