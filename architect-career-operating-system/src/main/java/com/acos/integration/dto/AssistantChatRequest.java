package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Ask-ACOS request. Identity comes from the caller JWT, not from this body.
 *
 * @param messages conversation turns for this request
 */
@Schema(name = "AssistantChatRequest", description = "Ask ACOS AI")
public record AssistantChatRequest(
    @Schema(
            description = "Conversation turns for this request",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "messages must not be empty") @Valid List<AssistantChatMessage> messages) {}
