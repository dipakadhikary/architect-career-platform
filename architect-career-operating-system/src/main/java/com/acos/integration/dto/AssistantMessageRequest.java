package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * One user message. The conversation id is a path parameter, not a body field.
 *
 * @param content question text
 */
@Schema(name = "AssistantMessageRequest", description = "Send a conversation message")
public record AssistantMessageRequest(
    @Schema(description = "Question text", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "content must not be blank") String content) {}
