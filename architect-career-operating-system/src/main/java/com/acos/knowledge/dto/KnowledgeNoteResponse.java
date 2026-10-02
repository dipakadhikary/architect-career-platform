package com.acos.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Knowledge note returned by the knowledge APIs.
 *
 * @param id note id
 * @param title note title
 * @param summary short summary
 * @param content markdown body
 * @param category optional category
 * @param tags assigned tag names
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "KnowledgeNoteResponse", description = "Markdown knowledge note")
public record KnowledgeNoteResponse(
    @Schema(
            description = "Note identifier",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Note title",
            example = "System Design Interview Notes",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(
            description = "Short summary",
            example = "Key patterns for distributed systems interviews",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String summary,
    @Schema(
            description = "Markdown body",
            example = "## CAP Theorem\n\nConsistency, Availability, Partition tolerance.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String content,
    @Schema(description = "Optional category", nullable = true) CategoryResponse category,
    @Schema(
            description = "Assigned tag names",
            example = "[\"interview\", \"distributed-systems\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> tags,
    @Schema(
            description = "Creation timestamp",
            example = "2026-08-04T06:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(
            description = "Last update timestamp",
            example = "2026-08-04T07:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable knowledge note response.
   *
   * @param id note id
   * @param title title
   * @param summary summary
   * @param content markdown content
   * @param category optional category
   * @param tags tag names
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public KnowledgeNoteResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(summary, "summary must not be null");
    Objects.requireNonNull(content, "content must not be null");
    Objects.requireNonNull(tags, "tags must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    tags = List.copyOf(tags);
  }
}
