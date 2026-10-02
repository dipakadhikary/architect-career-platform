package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Knowledge capability contract with the AI Platform. */
@FeignClient(
    name = "knowledge-ai",
    url = "${ai.platform.base-url}",
    configuration = AiPlatformFeignConfiguration.class)
public interface KnowledgeAiClient {

  /**
   * Indexes a knowledge document for later retrieval.
   *
   * @param request knowledge indexing request
   * @return indexing result
   */
  @PostMapping(
      value = "/api/v1/ai/knowledge/index",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  KnowledgeIndexResponse indexKnowledge(@RequestBody KnowledgeIndexRequest request);

  /**
   * Performs semantic search over indexed knowledge.
   *
   * @param request knowledge search request
   * @return ranked search results
   */
  @PostMapping(
      value = "/api/v1/ai/knowledge/search",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  KnowledgeSearchResponse searchKnowledge(@RequestBody KnowledgeSearchRequest request);

  /**
   * Summarizes knowledge content.
   *
   * @param request summarize request
   * @return summary result
   */
  @PostMapping(
      value = "/api/v1/ai/knowledge/summarize",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  KnowledgeSummarizeResponse summarizeKnowledge(@RequestBody KnowledgeSummarizeRequest request);
}
