package com.acos.integration.support;

import com.acos.integration.dto.KnowledgeIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Extension point for knowledge indexing failures. Current implementation logs only; future retry
 * mechanisms can replace or decorate this component.
 */
@Component
public class KnowledgeIndexingFailureHandler {

  private static final Logger LOG = LoggerFactory.getLogger(KnowledgeIndexingFailureHandler.class);

  /**
   * Records an indexing failure for a future retry mechanism.
   *
   * @param request original indexing request
   * @param cause failure cause
   */
  public void handle(KnowledgeIndexRequest request, Throwable cause) {
    if (LOG.isWarnEnabled()) {
      LOG.warn(
          "Knowledge indexing failure retained for future retry noteId={} userId={} causeType={}"
              + " message={}",
          request == null ? null : request.noteId(),
          request == null ? null : request.userId(),
          cause == null ? null : cause.getClass().getSimpleName(),
          cause == null ? null : cause.getMessage());
    }
  }
}
