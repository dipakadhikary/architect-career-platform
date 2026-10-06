package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * A controlled agent execution.
 *
 * @param executionId execution id
 * @param status lifecycle status
 * @param answer final answer, when one exists
 * @param errorCode empty when the run completed
 * @param sources citations from retrieval metadata
 * @param steps safe step labels
 * @param approvalRequired whether a human decision is pending
 * @param proposedAction description of the action waiting for approval
 */
@Schema(name = "AgentExecutionResponse", description = "Controlled agent execution")
public record AgentExecutionResponse(
    String executionId,
    String status,
    String answer,
    String errorCode,
    List<AssistantSource> sources,
    List<AgentStepResponse> steps,
    boolean approvalRequired,
    String proposedAction) {}
