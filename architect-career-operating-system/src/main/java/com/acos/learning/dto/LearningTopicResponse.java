package com.acos.learning.dto;

import com.acos.learning.entity.TopicStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Learning topic response.
 *
 * @param id topic id
 * @param title topic title
 * @param description optional description
 * @param status topic status
 * @param sortOrder sort order
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "LearningTopicResponse", description = "Learning topic")
public record LearningTopicResponse(
    @Schema(description = "Topic identifier", requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
    @Schema(description = "Topic title", requiredMode = Schema.RequiredMode.REQUIRED) String title,
    @Schema(description = "Optional description", nullable = true) String description,
    @Schema(description = "Topic status", requiredMode = Schema.RequiredMode.REQUIRED)
        TopicStatus status,
    @Schema(description = "Sort order", requiredMode = Schema.RequiredMode.REQUIRED) int sortOrder,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable topic response.
   *
   * @param id topic id
   * @param title title
   * @param description description
   * @param status status
   * @param sortOrder sort order
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public LearningTopicResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
