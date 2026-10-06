package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.AgentDecisionRequest;
import com.acos.integration.dto.AgentExecuteRequest;
import com.acos.integration.dto.AgentExecutionResponse;
import com.acos.integration.dto.AssistantChatRequest;
import com.acos.integration.dto.AssistantChatResponse;
import com.acos.integration.dto.AssistantConversationDetailResponse;
import com.acos.integration.dto.AssistantConversationPageResponse;
import com.acos.integration.dto.AssistantConversationResponse;
import com.acos.integration.dto.AssistantMessagePairResponse;
import com.acos.integration.dto.AssistantMessageRequest;
import com.acos.integration.dto.AssistantRenameRequest;
import com.acos.integration.dto.AuthoringEditRequest;
import com.acos.integration.dto.AuthoringProposalRequest;
import com.acos.integration.dto.AuthoringProposalResponse;
import com.acos.integration.dto.AuthoringRegenerateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

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

  /**
   * Creates an empty conversation for the forwarded caller.
   *
   * @return conversation metadata
   */
  @PostMapping(value = "/api/v1/ai/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
  AssistantConversationResponse createConversation();

  /**
   * Lists the forwarded caller's conversations.
   *
   * @param page zero-based page
   * @param size page size
   * @return one page
   */
  @GetMapping(value = "/api/v1/ai/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
  AssistantConversationPageResponse listConversations(
      @RequestParam("page") int page, @RequestParam("size") int size);

  /**
   * Loads one conversation when the forwarded caller owns it.
   *
   * @param conversationId conversation id
   * @return conversation and recent messages
   */
  @GetMapping(
      value = "/api/v1/ai/conversations/{conversationId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AssistantConversationDetailResponse getConversation(
      @PathVariable("conversationId") String conversationId);

  /**
   * Renames a conversation owned by the forwarded caller.
   *
   * @param conversationId conversation id
   * @param request replacement title
   * @return updated conversation
   */
  @PatchMapping(
      value = "/api/v1/ai/conversations/{conversationId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AssistantConversationResponse renameConversation(
      @PathVariable("conversationId") String conversationId,
      @RequestBody AssistantRenameRequest request);

  /**
   * Deletes a conversation owned by the forwarded caller.
   *
   * @param conversationId conversation id
   */
  @DeleteMapping(value = "/api/v1/ai/conversations/{conversationId}")
  void deleteConversation(@PathVariable("conversationId") String conversationId);

  /**
   * Sends a message and returns the stored user and assistant turns.
   *
   * @param conversationId conversation id
   * @param request message text
   * @param idempotencyKey optional duplicate-submit key
   * @return stored turns
   */
  @PostMapping(
      value = "/api/v1/ai/conversations/{conversationId}/messages",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AssistantMessagePairResponse sendMessage(
      @PathVariable("conversationId") String conversationId,
      @RequestBody AssistantMessageRequest request,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey);

  /**
   * Creates an AI draft. The Python service does not publish knowledge.
   *
   * @param request authorized authoring request
   * @return draft proposal
   */
  @PostMapping(
      value = "/api/v1/ai/authoring/proposals",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AuthoringProposalResponse generateProposal(@RequestBody AuthoringProposalRequest request);

  /**
   * Loads one draft owned by the forwarded caller.
   *
   * @param proposalId proposal id
   * @return draft proposal
   */
  @GetMapping(
      value = "/api/v1/ai/authoring/proposals/{proposalId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AuthoringProposalResponse getProposal(@PathVariable("proposalId") String proposalId);

  /**
   * Replaces draft text.
   *
   * @param proposalId proposal id
   * @param request edited markdown
   * @return updated draft
   */
  @PatchMapping(
      value = "/api/v1/ai/authoring/proposals/{proposalId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AuthoringProposalResponse editProposal(
      @PathVariable("proposalId") String proposalId, @RequestBody AuthoringEditRequest request);

  /**
   * Creates a new draft from the same source.
   *
   * @param proposalId previous proposal id
   * @param request optional instructions
   * @return new draft
   */
  @PostMapping(
      value = "/api/v1/ai/authoring/proposals/{proposalId}/regenerate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AuthoringProposalResponse regenerateProposal(
      @PathVariable("proposalId") String proposalId,
      @RequestBody AuthoringRegenerateRequest request);

  /**
   * Marks a draft accepted. This does not update ACOS knowledge.
   *
   * @param proposalId proposal id
   * @return accepted draft
   */
  @PostMapping(
      value = "/api/v1/ai/authoring/proposals/{proposalId}/accept",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AuthoringProposalResponse acceptProposal(@PathVariable("proposalId") String proposalId);

  /**
   * Marks a draft rejected.
   *
   * @param proposalId proposal id
   * @return rejected draft
   */
  @PostMapping(
      value = "/api/v1/ai/authoring/proposals/{proposalId}/reject",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AuthoringProposalResponse rejectProposal(@PathVariable("proposalId") String proposalId);

  /**
   * Runs a controlled agent goal for the forwarded caller.
   *
   * @param request goal
   * @return execution
   */
  @PostMapping(
      value = "/api/v1/ai/agents/execute",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AgentExecutionResponse executeAgent(@RequestBody AgentExecuteRequest request);

  /**
   * Loads one execution owned by the forwarded caller.
   *
   * @param executionId execution id
   * @return execution
   */
  @GetMapping(
      value = "/api/v1/ai/agents/executions/{executionId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AgentExecutionResponse getAgentExecution(@PathVariable("executionId") String executionId);

  /**
   * Cancels an execution before the next tool call.
   *
   * @param executionId execution id
   * @return execution
   */
  @PostMapping(
      value = "/api/v1/ai/agents/executions/{executionId}/cancel",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AgentExecutionResponse cancelAgentExecution(@PathVariable("executionId") String executionId);

  /**
   * Approves or rejects a proposed action. The Python service does not run high-risk tools.
   *
   * @param executionId execution id
   * @param request decision
   * @return execution
   */
  @PostMapping(
      value = "/api/v1/ai/agents/executions/{executionId}/decision",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  AgentExecutionResponse decideAgentExecution(
      @PathVariable("executionId") String executionId, @RequestBody AgentDecisionRequest request);
}
