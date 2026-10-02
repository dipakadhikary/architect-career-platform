package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Request to search indexed knowledge.
 *
 * @param userId owning user identifier
 * @param query search query
 * @param limit maximum number of hits to return
 */
@Schema(name = "KnowledgeSearchRequest", description = "Request to search indexed knowledge")
public record KnowledgeSearchRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(
            description = "Search query",
            example = "distributed consensus",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String query,
    @Schema(description = "Maximum number of hits to return", example = "10") Integer limit) {}
