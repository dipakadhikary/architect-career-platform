package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Provider-neutral assistant answer.
 *
 * @param answer markdown answer
 * @param model model name reported by the provider
 * @param provider provider name
 * @param grounded whether the answer used ACOS knowledge context
 * @param sources citations from retrieved metadata
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
        String provider,
    @Schema(description = "True when the answer used ACOS knowledge context") boolean grounded,
    @Schema(description = "Citations built from retrieved ACOS metadata")
        java.util.List<AssistantSource> sources) {}
