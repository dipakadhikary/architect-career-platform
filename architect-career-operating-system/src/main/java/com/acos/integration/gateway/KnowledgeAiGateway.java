package com.acos.integration.gateway;

import com.acos.integration.capability.KnowledgeAiCapability;
import com.acos.integration.client.KnowledgeAiClient;
import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;
import com.acos.integration.support.AiPlatformInvoker;
import com.acos.integration.support.AiPlatformMockResponses;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Knowledge AI gateway. Routes to Feign when enabled, otherwise returns mocked successful
 * responses.
 */
@Service
public class KnowledgeAiGateway implements KnowledgeAiCapability {

  private static final String FEATURE = "knowledge";

  private final ObjectProvider<KnowledgeAiClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client (absent when AI Platform is disabled)
   * @param invoker AI Platform invoker
   */
  public KnowledgeAiGateway(ObjectProvider<KnowledgeAiClient> client, AiPlatformInvoker invoker) {
    this.client = client;
    this.invoker = invoker;
  }

  /**
   * Indexes knowledge content.
   *
   * @param request index request
   * @return index response
   */
  @Override
  public KnowledgeIndexResponse indexKnowledge(KnowledgeIndexRequest request) {
    return invoker.execute(
        FEATURE,
        "index",
        "/api/v1/ai/knowledge/index",
        () -> requireClient().indexKnowledge(request),
        () -> AiPlatformMockResponses.indexKnowledge(request));
  }

  /**
   * Performs semantic knowledge search.
   *
   * @param request search request
   * @return search response
   */
  @Override
  public KnowledgeSearchResponse searchKnowledge(KnowledgeSearchRequest request) {
    return invoker.execute(
        FEATURE,
        "semantic-search",
        "/api/v1/ai/knowledge/search",
        () -> requireClient().searchKnowledge(request),
        () -> AiPlatformMockResponses.searchKnowledge(request));
  }

  /**
   * Summarizes knowledge content.
   *
   * @param request summarize request
   * @return summarize response
   */
  @Override
  public KnowledgeSummarizeResponse summarizeKnowledge(KnowledgeSummarizeRequest request) {
    return invoker.execute(
        FEATURE,
        "summarize",
        "/api/v1/ai/knowledge/summarize",
        () -> requireClient().summarizeKnowledge(request),
        () -> AiPlatformMockResponses.summarizeKnowledge(request));
  }

  private KnowledgeAiClient requireClient() {
    KnowledgeAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new IllegalStateException("KnowledgeAiClient is not available");
    }
    return resolved;
  }
}
