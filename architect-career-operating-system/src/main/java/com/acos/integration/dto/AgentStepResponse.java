package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A safe agent step. Arguments and chain-of-thought are not included.
 *
 * @param stepId step id
 * @param tool registered tool name
 * @param label user-facing status
 * @param status step status
 */
@Schema(name = "AgentStepResponse", description = "One controlled agent step")
public record AgentStepResponse(String stepId, String tool, String label, String status) {}
