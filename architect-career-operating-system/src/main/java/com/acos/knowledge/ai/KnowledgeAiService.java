package com.acos.knowledge.ai;

import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;
import com.acos.integration.facade.KnowledgeAiFacade;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Knowledge AI extension points. Automatic indexing is handled by the integration event listener;
 * this service exposes on-demand capabilities for future controllers or workflows.
 *
 * <p>Depends only on {@link KnowledgeAiFacade}; never on OpenFeign clients.
 */
@Service
public class KnowledgeAiService {

  private final KnowledgeAiFacade knowledgeAiFacade;

  /**
   * Creates the knowledge AI service.
   *
   * @param knowledgeAiFacade knowledge AI facade
   */
  public KnowledgeAiService(KnowledgeAiFacade knowledgeAiFacade) {
    this.knowledgeAiFacade =
        Objects.requireNonNull(knowledgeAiFacade, "knowledgeAiFacade must not be null");
  }

  /**
   * Performs semantic knowledge search via the AI facade.
   *
   * @param request search request
   * @return search response (empty fallback when AI is unavailable)
   */
  public KnowledgeSearchResponse searchKnowledge(KnowledgeSearchRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return knowledgeAiFacade.searchKnowledge(request);
  }

  /**
   * Summarizes knowledge content via the AI facade.
   *
   * @param request summarize request
   * @return summarize response (empty fallback when AI is unavailable)
   */
  public KnowledgeSummarizeResponse summarizeKnowledge(KnowledgeSummarizeRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return knowledgeAiFacade.summarizeKnowledge(request);
  }
}
