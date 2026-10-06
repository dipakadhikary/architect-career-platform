package com.acos.integration.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import com.acos.integration.dto.AgentDecisionRequest;
import com.acos.integration.dto.AgentExecuteRequest;
import com.acos.integration.dto.AgentExecutionResponse;
import com.acos.integration.gateway.AssistantAiGateway;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Authenticated pass-through to the controlled Python agent. */
@RestController
@Validated
@RequestMapping("/api/v1/integration/ai/agents")
@Tag(name = "AI Agent", description = "Controlled read-only ACOS agent")
@SecurityRequirement(name = "bearer-jwt")
public class AgentAiController {

  private final AssistantAiGateway assistantAiGateway;

  /**
   * Creates the controller.
   *
   * @param assistantAiGateway assistant gateway
   */
  public AgentAiController(AssistantAiGateway assistantAiGateway) {
    this.assistantAiGateway =
        Objects.requireNonNull(assistantAiGateway, "assistantAiGateway must not be null");
  }

  /**
   * Runs a goal. The caller identity is the authenticated principal.
   *
   * @param principal authenticated user
   * @param request goal
   * @return execution
   */
  @PostMapping(
      path = "/execute",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Run a controlled agent goal")
  public ResponseEntity<ApiResponse<AgentExecutionResponse>> execute(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody AgentExecuteRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(ApiResponse.success(assistantAiGateway.executeAgent(request)));
  }

  /**
   * Loads an execution owned by the caller.
   *
   * @param principal authenticated user
   * @param executionId execution id
   * @return execution
   */
  @GetMapping(path = "/executions/{executionId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Load an agent execution")
  public ResponseEntity<ApiResponse<AgentExecutionResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String executionId) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(assistantAiGateway.getAgentExecution(executionId)));
  }

  /**
   * Cancels an execution before the next tool call.
   *
   * @param principal authenticated user
   * @param executionId execution id
   * @return execution
   */
  @PostMapping(
      path = "/executions/{executionId}/cancel",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Cancel an agent execution")
  public ResponseEntity<ApiResponse<AgentExecutionResponse>> cancel(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable String executionId) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(assistantAiGateway.cancelAgentExecution(executionId)));
  }

  /**
   * Approves or rejects a proposed action. High-risk tools are not executed.
   *
   * @param principal authenticated user
   * @param executionId execution id
   * @param request decision
   * @return execution
   */
  @PostMapping(
      path = "/executions/{executionId}/decision",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Approve or reject a proposed agent action")
  public ResponseEntity<ApiResponse<AgentExecutionResponse>> decide(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable String executionId,
      @Valid @RequestBody AgentDecisionRequest request) {
    requirePrincipal(principal);
    return ResponseEntity.ok(
        ApiResponse.success(assistantAiGateway.decideAgentExecution(executionId, request)));
  }

  private static void requirePrincipal(AcosUserDetails principal) {
    if (principal == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication is required");
    }
  }
}
