package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Request to summarize knowledge content.
 *
 * @param userId owning user identifier
 * @param noteId optional source note identifier
 * @param content content to summarize
 * @param maxLength optional maximum summary length in characters
 */
@Schema(name = "KnowledgeSummarizeRequest", description = "Request to summarize knowledge content")
public record KnowledgeSummarizeRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(description = "Optional source note identifier") UUID noteId,
    @Schema(description = "Content to summarize", requiredMode = Schema.RequiredMode.REQUIRED)
        String content,
    @Schema(description = "Optional maximum summary length in characters", example = "500")
        Integer maxLength) {}
