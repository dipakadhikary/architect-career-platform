package com.acos.integration.capability;

import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;

/** Knowledge AI capability port for the Business Platform. */
public interface KnowledgeAiCapability {

  /**
   * Indexes a knowledge document.
   *
   * @param request index request
   * @return index response
   */
  KnowledgeIndexResponse indexKnowledge(KnowledgeIndexRequest request);

  /**
   * Performs semantic knowledge search.
   *
   * @param request search request
   * @return search response
   */
  KnowledgeSearchResponse searchKnowledge(KnowledgeSearchRequest request);

  /**
   * Summarizes knowledge content.
   *
   * @param request summarize request
   * @return summarize response
   */
  KnowledgeSummarizeResponse summarizeKnowledge(KnowledgeSummarizeRequest request);
}
