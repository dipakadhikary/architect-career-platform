package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * One chat turn supplied by the authenticated caller.
 *
 * @param role system, user, or assistant
 * @param content message text
 */
@Schema(name = "AssistantChatMessage", description = "One assistant conversation turn")
public record AssistantChatMessage(
    @Schema(
            description = "Message role",
            example = "user",
            allowableValues = {"system", "user", "assistant"},
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "role must not be blank") @Pattern(
            regexp = "system|user|assistant",
            message = "role must be system, user, or assistant")
        String role,
    @Schema(
            description = "Message text",
            example = "Explain dependency injection",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "content must not be blank") @Size(max = 8000, message = "content must not exceed 8000 characters") String content) {}
