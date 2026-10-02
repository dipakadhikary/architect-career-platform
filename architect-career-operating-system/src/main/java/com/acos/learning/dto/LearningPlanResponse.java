package com.acos.learning.dto;

import com.acos.learning.entity.LearningPlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Learning plan response with nested milestones and progress.
 *
 * @param id plan id
 * @param title plan title
 * @param description optional description
 * @param status plan status
 * @param targetDate optional target date
 * @param progressPercent completion percentage based on topics
 * @param totalTopics total topics across milestones
 * @param completedTopics completed topics across milestones
 * @param milestones nested milestones
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "LearningPlanResponse", description = "Learning plan with milestones and progress")
public record LearningPlanResponse(
    @Schema(description = "Plan identifier", requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
    @Schema(description = "Plan title", requiredMode = Schema.RequiredMode.REQUIRED) String title,
    @Schema(description = "Optional description", nullable = true) String description,
    @Schema(description = "Plan status", requiredMode = Schema.RequiredMode.REQUIRED)
        LearningPlanStatus status,
    @Schema(description = "Optional target date", nullable = true) LocalDate targetDate,
    @Schema(description = "Progress percentage 0-100", requiredMode = Schema.RequiredMode.REQUIRED)
        int progressPercent,
    @Schema(description = "Total topics", requiredMode = Schema.RequiredMode.REQUIRED)
        int totalTopics,
    @Schema(description = "Completed topics", requiredMode = Schema.RequiredMode.REQUIRED)
        int completedTopics,
    @Schema(description = "Milestones", requiredMode = Schema.RequiredMode.REQUIRED)
        List<LearningMilestoneResponse> milestones,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable plan response.
   *
   * @param id plan id
   * @param title title
   * @param description description
   * @param status status
   * @param targetDate target date
   * @param progressPercent progress percent
   * @param totalTopics total topics
   * @param completedTopics completed topics
   * @param milestones milestones
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public LearningPlanResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(milestones, "milestones must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    milestones = List.copyOf(milestones);
  }
}
