package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A goal for the controlled agent. Identity is the authenticated caller.
 *
 * @param goal what the user wants done
 * @param conversationId optional owned conversation
 */
@Schema(name = "AgentExecuteRequest", description = "Controlled agent goal")
public record AgentExecuteRequest(@NotBlank @Size(max = 8000) String goal, String conversationId) {}
