package com.acos.integration.facade;

import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;
import com.acos.integration.gateway.KnowledgeAiGateway;
import com.acos.integration.support.KnowledgeIndexingFailureHandler;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Knowledge AI facade between business services and the OpenFeign knowledge client. Applies feature
 * toggle, validation, metrics/logging (via gateway/invoker), and graceful fallbacks.
 */
@Service
public class KnowledgeAiFacade {

  private final KnowledgeAiGateway knowledgeAiGateway;
  private final AiFacadeSupport aiFacadeSupport;
  private final KnowledgeIndexingFailureHandler knowledgeIndexingFailureHandler;

  /**
   * Creates the facade.
   *
   * @param knowledgeAiGateway knowledge gateway
   * @param aiFacadeSupport shared facade support
   * @param knowledgeIndexingFailureHandler indexing failure extension point
   */
  public KnowledgeAiFacade(
      KnowledgeAiGateway knowledgeAiGateway,
      AiFacadeSupport aiFacadeSupport,
      KnowledgeIndexingFailureHandler knowledgeIndexingFailureHandler) {
    this.knowledgeAiGateway =
        Objects.requireNonNull(knowledgeAiGateway, "knowledgeAiGateway must not be null");
    this.aiFacadeSupport =
        Objects.requireNonNull(aiFacadeSupport, "aiFacadeSupport must not be null");
    this.knowledgeIndexingFailureHandler =
        Objects.requireNonNull(
            knowledgeIndexingFailureHandler, "knowledgeIndexingFailureHandler must not be null");
  }

  /**
   * Indexes knowledge content, acknowledging success when AI is disabled or unavailable.
   *
   * @param request index request
   * @return index or acknowledgement response
   */
  public KnowledgeIndexResponse indexKnowledge(KnowledgeIndexRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.noteId(), "noteId must not be null");
    Objects.requireNonNull(request.title(), "title must not be null");
    Objects.requireNonNull(request.content(), "content must not be null");

    return aiFacadeSupport.execute(
        "knowledge",
        "index",
        () -> knowledgeAiGateway.indexKnowledge(request),
        () -> AiPlatformFallbacks.indexAcknowledged(request),
        cause -> knowledgeIndexingFailureHandler.handle(request, cause));
  }

  /**
   * Performs semantic knowledge search with an empty fallback.
   *
   * @param request search request
   * @return search response
   */
  public KnowledgeSearchResponse searchKnowledge(KnowledgeSearchRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.query(), "query must not be null");
    if (request.query().isBlank()) {
      throw new IllegalArgumentException("query must not be blank");
    }

    return aiFacadeSupport.execute(
        "knowledge",
        "semantic-search",
        () -> knowledgeAiGateway.searchKnowledge(request),
        AiPlatformFallbacks::emptySearch,
        null);
  }

  /**
   * Summarizes knowledge content with an empty fallback.
   *
   * @param request summarize request
   * @return summarize response
   */
  public KnowledgeSummarizeResponse summarizeKnowledge(KnowledgeSummarizeRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.content(), "content must not be null");

    return aiFacadeSupport.execute(
        "knowledge",
        "summarize",
        () -> knowledgeAiGateway.summarizeKnowledge(request),
        () -> AiPlatformFallbacks.emptySummary(request),
        null);
  }
}
