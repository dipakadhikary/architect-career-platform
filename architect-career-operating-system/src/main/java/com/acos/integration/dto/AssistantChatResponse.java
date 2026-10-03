package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Provider-neutral assistant answer.
 *
 * @param answer markdown answer
 * @param model model name reported by the provider
 * @param provider provider name
 */
@Schema(name = "AssistantChatResponse", description = "Normalized ACOS AI answer")
public record AssistantChatResponse(
    @Schema(description = "Markdown answer", requiredMode = Schema.RequiredMode.REQUIRED)
        String answer,
    @Schema(description = "Model name", requiredMode = Schema.RequiredMode.REQUIRED) String model,
    @Schema(
            description = "Provider name",
            example = "openai",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String provider) {}
