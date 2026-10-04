package com.acos.integration.gateway;

import com.acos.integration.client.AssistantAiClient;
import com.acos.integration.dto.AssistantChatRequest;
import com.acos.integration.dto.AssistantChatResponse;
import com.acos.integration.dto.AssistantConversationDetailResponse;
import com.acos.integration.dto.AssistantConversationPageResponse;
import com.acos.integration.dto.AssistantConversationResponse;
import com.acos.integration.dto.AssistantMessagePairResponse;
import com.acos.integration.dto.AssistantMessageRequest;
import com.acos.integration.dto.AssistantRenameRequest;
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

  /**
   * Creates a conversation for the authenticated caller.
   *
   * @return conversation metadata
   */
  public AssistantConversationResponse createConversation() {
    return invoker.execute(
        FEATURE,
        "conversation-create",
        "/api/v1/ai/conversations",
        () -> requireClient().createConversation(),
        AssistantAiGateway::disabled);
  }

  /**
   * Lists the authenticated caller's conversations.
   *
   * @param page zero-based page
   * @param size page size
   * @return one page
   */
  public AssistantConversationPageResponse listConversations(int page, int size) {
    return invoker.execute(
        FEATURE,
        "conversation-list",
        "/api/v1/ai/conversations",
        () -> requireClient().listConversations(page, size),
        AssistantAiGateway::disabled);
  }

  /**
   * Loads one owned conversation.
   *
   * @param conversationId conversation id
   * @return conversation and recent messages
   */
  public AssistantConversationDetailResponse getConversation(String conversationId) {
    return invoker.execute(
        FEATURE,
        "conversation-get",
        "/api/v1/ai/conversations/{id}",
        () -> requireClient().getConversation(conversationId),
        AssistantAiGateway::disabled);
  }

  /**
   * Renames an owned conversation.
   *
   * @param conversationId conversation id
   * @param request replacement title
   * @return updated conversation
   */
  public AssistantConversationResponse renameConversation(
      String conversationId, AssistantRenameRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return invoker.execute(
        FEATURE,
        "conversation-rename",
        "/api/v1/ai/conversations/{id}",
        () -> requireClient().renameConversation(conversationId, request),
        AssistantAiGateway::disabled);
  }

  /**
   * Deletes an owned conversation and its messages.
   *
   * @param conversationId conversation id
   */
  public void deleteConversation(String conversationId) {
    invoker.execute(
        FEATURE,
        "conversation-delete",
        "/api/v1/ai/conversations/{id}",
        () -> {
          requireClient().deleteConversation(conversationId);
          return null;
        },
        AssistantAiGateway::disabled);
  }

  /**
   * Sends a message through the existing assistant pipeline.
   *
   * @param conversationId conversation id
   * @param request message text
   * @param idempotencyKey optional duplicate-submit key
   * @return stored user and assistant messages
   */
  public AssistantMessagePairResponse sendMessage(
      String conversationId, AssistantMessageRequest request, String idempotencyKey) {
    Objects.requireNonNull(request, "request must not be null");
    return invoker.execute(
        FEATURE,
        "conversation-message",
        "/api/v1/ai/conversations/{id}/messages",
        () -> requireClient().sendMessage(conversationId, request, idempotencyKey),
        AssistantAiGateway::disabled);
  }

  private static <T> T disabled() {
    throw new AiPlatformUnavailableException("ACOS AI is disabled");
  }

  private AssistantAiClient requireClient() {
    AssistantAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new AiPlatformUnavailableException("ACOS AI client is not available");
    }
    return resolved;
  }
}
