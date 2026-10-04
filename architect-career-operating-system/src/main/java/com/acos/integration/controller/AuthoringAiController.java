package com.acos.integration.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import com.acos.integration.dto.AuthoringEditRequest;
import com.acos.integration.dto.AuthoringProposalRequest;
import com.acos.integration.dto.AuthoringProposalResponse;
import com.acos.integration.dto.AuthoringRegenerateRequest;
import com.acos.integration.gateway.AuthoringAiGateway;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Authenticated authoring pass-through. Knowledge saves stay on the knowledge API. */
@RestController
@Validated
@RequestMapping("/api/v1/integration/ai/authoring")
@Tag(name = "AI Authoring", description = "AI drafts for ACOS knowledge")
@SecurityRequirement(name = "bearer-jwt")
public class AuthoringAiController {

  private final AuthoringAiGateway authoringAiGateway;
  private final KnowledgeService knowledgeService;

  /**
   * Creates the controller.
   *
   * @param authoringAiGateway authoring gateway
   * @param knowledgeService knowledge service used to authorize source notes
   */
  public AuthoringAiController(
      AuthoringAiGateway authoringAiGateway, KnowledgeService knowledgeService) {
    this.authoringAiGateway =
        Objects.requireNonNull(authoringAiGateway, "authoringAiGateway must not be null");
    this.knowledgeService =
        Objects.requireNonNull(knowledgeService, "knowledgeService must not be null");
  }

  /**
   * Creates a draft. When a note id is present, ACOS loads that owned note first.
   *
   * @param principal authenticated user
   * @param request authoring request
   * @return draft proposal
   */
  @PostMapping(
      path = "/proposals",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Generate an AI knowledge draft")
  public ResponseEntity<ApiResponse<AuthoringProposalResponse>> generate(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody AuthoringProposalRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(authoringAiGateway.generate(authorized(principal, request))));
  }

  /**
   * Loads one draft.
   *
   * @param principal authenticated user
   * @param proposalId proposal id
   * @return draft proposal
   */
  @GetMapping(path = "/proposals/{proposalId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get an AI knowledge draft")
  public ResponseEntity<ApiResponse<AuthoringProposalResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String proposalId) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(authoringAiGateway.get(proposalId)));
  }

  /**
   * Edits draft text without updating the knowledge note.
   *
   * @param principal authenticated user
   * @param proposalId proposal id
   * @param request edited markdown
   * @return updated draft
   */
  @PatchMapping(
      path = "/proposals/{proposalId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Edit an AI knowledge draft")
  public ResponseEntity<ApiResponse<AuthoringProposalResponse>> edit(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable String proposalId,
      @Valid @RequestBody AuthoringEditRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(authoringAiGateway.edit(proposalId, request)));
  }

  /**
   * Creates another draft from the same source.
   *
   * @param principal authenticated user
   * @param proposalId previous proposal id
   * @param request optional instructions
   * @return new draft
   */
  @PostMapping(
      path = "/proposals/{proposalId}/regenerate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Regenerate an AI knowledge draft")
  public ResponseEntity<ApiResponse<AuthoringProposalResponse>> regenerate(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable String proposalId,
      @RequestBody(required = false) AuthoringRegenerateRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(authoringAiGateway.regenerate(proposalId, request)));
  }

  /**
   * Accepts a draft for a later explicit knowledge save.
   *
   * @param principal authenticated user
   * @param proposalId proposal id
   * @return accepted draft
   */
  @PostMapping(path = "/proposals/{proposalId}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Accept an AI knowledge draft")
  public ResponseEntity<ApiResponse<AuthoringProposalResponse>> accept(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String proposalId) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(authoringAiGateway.accept(proposalId)));
  }

  /**
   * Rejects a draft.
   *
   * @param principal authenticated user
   * @param proposalId proposal id
   * @return rejected draft
   */
  @PostMapping(path = "/proposals/{proposalId}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Reject an AI knowledge draft")
  public ResponseEntity<ApiResponse<AuthoringProposalResponse>> reject(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String proposalId) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(authoringAiGateway.reject(proposalId)));
  }

  private AuthoringProposalRequest authorized(
      AcosUserDetails principal, AuthoringProposalRequest request) {
    String source = request.sourceContent();
    Long version = request.sourceVersion();
    if (request.contentId() != null) {
      KnowledgeNoteResponse note = knowledgeService.get(principal.getId(), request.contentId());
      source = note.content();
      version = note.version();
    }
    int questions = request.questionCount() == null ? 5 : request.questionCount();
    return new AuthoringProposalRequest(
        request.operation(),
        request.topic(),
        request.instructions(),
        source,
        request.contentId(),
        version,
        request.difficulty(),
        questions,
        request.conversationId(),
        request.useConversation(),
        request.useKnowledge());
  }

  private static void requirePrincipal(AcosUserDetails principal) {
    if (principal == null || principal.getId() == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required");
    }
  }
}
