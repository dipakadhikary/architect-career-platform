package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.AssistantChatRequest;
import com.acos.integration.dto.AssistantChatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Assistant chat contract with the Python AI service. */
@FeignClient(
    name = "assistant-ai",
    url = "${ai.platform.base-url}",
    configuration = AiPlatformFeignConfiguration.class)
public interface AssistantAiClient {

  /**
   * Asks the Python assistant. The forwarded bearer token is the caller identity.
   *
   * @param request chat request without a caller-supplied user id
   * @return normalized answer
   */
  @PostMapping(
      value = "/api/v1/ai/chat",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AssistantChatResponse chat(@RequestBody AssistantChatRequest request);
}
