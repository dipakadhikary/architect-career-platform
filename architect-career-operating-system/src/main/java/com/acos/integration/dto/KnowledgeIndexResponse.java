package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * Response after indexing a knowledge document.
 *
 * @param documentId AI Platform document identifier
 * @param noteId source knowledge note identifier
 * @param status indexing status
 * @param indexedAt indexing timestamp
 */
@Schema(
    name = "KnowledgeIndexResponse",
    description = "Response after indexing a knowledge document")
public record KnowledgeIndexResponse(
    @Schema(description = "AI Platform document identifier") UUID documentId,
    @Schema(description = "Source knowledge note identifier") UUID noteId,
    @Schema(description = "Indexing status", example = "INDEXED") String status,
    @Schema(description = "Indexing timestamp") Instant indexedAt) {}
