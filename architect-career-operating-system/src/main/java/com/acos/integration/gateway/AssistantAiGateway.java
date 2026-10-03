package com.acos.integration.gateway;

import com.acos.integration.client.AssistantAiClient;
import com.acos.integration.dto.AssistantChatRequest;
import com.acos.integration.dto.AssistantChatResponse;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.support.AiPlatformInvoker;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Forwards Ask ACOS calls to the Python assistant. Disabled integration fails the call instead of
 * inventing an answer.
 */
@Service
public class AssistantAiGateway {

  private static final String FEATURE = "assistant";
  private static final String CAPABILITY = "chat";
  private static final String ENDPOINT = "/api/v1/ai/chat";

  private final ObjectProvider<AssistantAiClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client
   * @param invoker resilience invoker
   */
  public AssistantAiGateway(ObjectProvider<AssistantAiClient> client, AiPlatformInvoker invoker) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.invoker = Objects.requireNonNull(invoker, "invoker must not be null");
  }

  /**
   * Asks the assistant.
   *
   * @param request validated chat request
   * @return normalized answer
   */
  public AssistantChatResponse ask(AssistantChatRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return invoker.execute(
        FEATURE,
        CAPABILITY,
        ENDPOINT,
        () -> requireClient().chat(request),
        () -> {
          throw new AiPlatformUnavailableException("ACOS AI is disabled");
        });
  }

  private AssistantAiClient requireClient() {
    AssistantAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new AiPlatformUnavailableException("ACOS AI client is not available");
    }
    return resolved;
  }
}
