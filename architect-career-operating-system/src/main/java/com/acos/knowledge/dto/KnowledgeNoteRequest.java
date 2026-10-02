package com.acos.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Payload used to create or update a markdown knowledge note.
 *
 * @param title note title
 * @param summary short summary used for search
 * @param content markdown body
 * @param categoryName optional category name (created when missing); blank clears category on
 *     update
 * @param tagNames optional tag names; on create {@code null} means no tags; on update {@code null}
 *     leaves tags unchanged and an empty list clears tags
 */
@Schema(
    name = "KnowledgeNoteRequest",
    description = "Payload used to create or update a knowledge note")
public record KnowledgeNoteRequest(
    @Schema(
            description = "Note title",
            example = "System Design Interview Notes",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(
            description = "Short summary used for search",
            example = "Key patterns for distributed systems interviews",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "summary must not be blank") @Size(max = 500, message = "summary must not exceed 500 characters") String summary,
    @Schema(
            description = "Markdown body",
            example = "## CAP Theorem\n\nConsistency, Availability, Partition tolerance.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "content must not be blank") String content,
    @Schema(
            description = "Optional category name; created for the owner when missing",
            example = "System Design",
            maxLength = 100,
            nullable = true)
        @Size(max = 100, message = "categoryName must not exceed 100 characters") String categoryName,
    @Schema(
            description = "Optional tag names; created for the owner when missing",
            example = "[\"interview\", \"distributed-systems\"]",
            nullable = true)
        List<
                @NotBlank(message = "tag name must not be blank") @Size(max = 50, message = "tag name must not exceed 50 characters") String>
            tagNames) {}
