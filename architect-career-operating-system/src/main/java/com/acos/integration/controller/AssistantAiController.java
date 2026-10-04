package com.acos.integration.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import com.acos.integration.dto.AssistantChatRequest;
import com.acos.integration.dto.AssistantChatResponse;
import com.acos.integration.dto.AssistantConversationDetailResponse;
import com.acos.integration.dto.AssistantConversationPageResponse;
import com.acos.integration.dto.AssistantConversationResponse;
import com.acos.integration.dto.AssistantMessagePairResponse;
import com.acos.integration.dto.AssistantMessageRequest;
import com.acos.integration.dto.AssistantRenameRequest;
import com.acos.integration.gateway.AssistantAiGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Authenticated pass-through to the Python Ask ACOS assistant. */
@RestController
@Validated
@RequestMapping("/api/v1/integration/ai")
@Tag(name = "AI Integration", description = "Authenticated ACOS AI assistant")
@SecurityRequirement(name = "bearer-jwt")
public class AssistantAiController {

  private final AssistantAiGateway assistantAiGateway;

  /**
   * Creates the controller.
   *
   * @param assistantAiGateway assistant gateway
   */
  public AssistantAiController(AssistantAiGateway assistantAiGateway) {
    this.assistantAiGateway =
        Objects.requireNonNull(assistantAiGateway, "assistantAiGateway must not be null");
  }

  /**
   * Asks ACOS AI. The caller identity is the authenticated principal, not a body field.
   *
   * @param principal authenticated user
   * @param request message list
   * @return normalized answer
   */
  @PostMapping(
      path = "/chat",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Ask ACOS AI",
      description = "Returns a general AI answer. Does not search ACOS knowledge.")
  public ResponseEntity<ApiResponse<AssistantChatResponse>> chat(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody AssistantChatRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(assistantAiGateway.ask(request)));
  }

  /**
   * Starts a conversation for the authenticated user.
   *
   * @param principal authenticated user
   * @return conversation metadata
   */
  @PostMapping(path = "/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create an AI conversation")
  public ResponseEntity<ApiResponse<AssistantConversationResponse>> createConversation(
      @AuthenticationPrincipal AcosUserDetails principal) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(assistantAiGateway.createConversation()));
  }

  /**
   * Lists the authenticated user's conversations.
   *
   * @param principal authenticated user
   * @param page zero-based page
   * @param size page size
   * @return one page
   */
  @GetMapping(path = "/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List AI conversations")
  public ResponseEntity<ApiResponse<AssistantConversationPageResponse>> listConversations(
      @AuthenticationPrincipal AcosUserDetails principal,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(assistantAiGateway.listConversations(page, size)));
  }

  /**
   * Opens one conversation owned by the authenticated user.
   *
   * @param principal authenticated user
   * @param conversationId conversation id
   * @return conversation and recent messages
   */
  @GetMapping(path = "/conversations/{conversationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get an AI conversation")
  public ResponseEntity<ApiResponse<AssistantConversationDetailResponse>> getConversation(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String conversationId) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(assistantAiGateway.getConversation(conversationId)));
  }

  /**
   * Renames a conversation owned by the authenticated user.
   *
   * @param principal authenticated user
   * @param conversationId conversation id
   * @param request replacement title
   * @return updated conversation
   */
  @PatchMapping(
      path = "/conversations/{conversationId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Rename an AI conversation")
  public ResponseEntity<ApiResponse<AssistantConversationResponse>> renameConversation(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable String conversationId,
      @Valid @RequestBody AssistantRenameRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(assistantAiGateway.renameConversation(conversationId, request)));
  }

  /**
   * Deletes a conversation owned by the authenticated user.
   *
   * @param principal authenticated user
   * @param conversationId conversation id
   */
  @DeleteMapping(path = "/conversations/{conversationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete an AI conversation")
  public void deleteConversation(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String conversationId) {
    requirePrincipal(principal);
    assistantAiGateway.deleteConversation(conversationId);
  }

  /**
   * Continues a conversation. Identity is the authenticated principal.
   *
   * @param principal authenticated user
   * @param conversationId conversation id
   * @param request message text
   * @param idempotencyKey optional duplicate-submit key
   * @return stored user and assistant messages
   */
  @PostMapping(
      path = "/conversations/{conversationId}/messages",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Send an AI conversation message")
  public ResponseEntity<ApiResponse<AssistantMessagePairResponse>> sendMessage(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable String conversationId,
      @Valid @RequestBody AssistantMessageRequest request,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(
            assistantAiGateway.sendMessage(conversationId, request, idempotencyKey)));
  }

  private static void requirePrincipal(AcosUserDetails principal) {
    if (principal == null || principal.getId() == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required");
    }
  }
}
