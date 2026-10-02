package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to index a knowledge document in the AI Platform.
 *
 * @param userId owning user identifier
 * @param noteId source knowledge note identifier
 * @param title document title
 * @param content document body
 * @param tags optional classification tags
 */
@Schema(name = "KnowledgeIndexRequest", description = "Request to index a knowledge document")
public record KnowledgeIndexRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(
            description = "Source knowledge note identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID noteId,
    @Schema(
            description = "Document title",
            example = "CAP Theorem",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(description = "Document body", requiredMode = Schema.RequiredMode.REQUIRED)
        String content,
    @Schema(description = "Optional classification tags", example = "[\"system-design\"]")
        List<String> tags) {}
