package com.acos.learning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Learning milestone response with nested topics and progress.
 *
 * @param id milestone id
 * @param title milestone title
 * @param description optional description
 * @param sortOrder sort order
 * @param targetDate optional target date
 * @param progressPercent completion percentage based on topics
 * @param totalTopics total topics
 * @param completedTopics completed topics
 * @param topics nested topics
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "LearningMilestoneResponse", description = "Learning milestone with topics")
public record LearningMilestoneResponse(
    @Schema(description = "Milestone identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Milestone title", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(description = "Optional description", nullable = true) String description,
    @Schema(description = "Sort order", requiredMode = Schema.RequiredMode.REQUIRED) int sortOrder,
    @Schema(description = "Optional target date", nullable = true) LocalDate targetDate,
    @Schema(description = "Progress percentage 0-100", requiredMode = Schema.RequiredMode.REQUIRED)
        int progressPercent,
    @Schema(description = "Total topics", requiredMode = Schema.RequiredMode.REQUIRED)
        int totalTopics,
    @Schema(description = "Completed topics", requiredMode = Schema.RequiredMode.REQUIRED)
        int completedTopics,
    @Schema(description = "Topics", requiredMode = Schema.RequiredMode.REQUIRED)
        List<LearningTopicResponse> topics,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable milestone response.
   *
   * @param id milestone id
   * @param title title
   * @param description description
   * @param sortOrder sort order
   * @param targetDate target date
   * @param progressPercent progress percent
   * @param totalTopics total topics
   * @param completedTopics completed topics
   * @param topics topics
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public LearningMilestoneResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(topics, "topics must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    topics = List.copyOf(topics);
  }
}
