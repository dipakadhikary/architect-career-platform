package com.acos.career.dto;

import com.acos.career.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;

/**
 * A single milestone step in an application timeline visualization.
 *
 * @param status milestone status
 * @param label human-readable milestone label
 * @param reached whether this milestone has been reached
 * @param current whether this milestone is the application's current status
 * @param changedAt timestamp the milestone was reached, {@code null} when not yet reached
 */
@Schema(name = "TimelineStepResponse", description = "Application timeline milestone step")
public record TimelineStepResponse(
    @Schema(description = "Milestone status", requiredMode = Schema.RequiredMode.REQUIRED)
        ApplicationStatus status,
    @Schema(
            description = "Human-readable milestone label",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String label,
    @Schema(
            description = "Whether this milestone has been reached",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean reached,
    @Schema(
            description = "Whether this milestone is the application's current status",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean current,
    @Schema(description = "Timestamp the milestone was reached", nullable = true)
        Instant changedAt) {

  /**
   * Creates an immutable timeline step.
   *
   * @param status milestone status
   * @param label milestone label
   * @param reached reached flag
   * @param current current flag
   * @param changedAt reached-at timestamp
   */
  public TimelineStepResponse {
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(label, "label must not be null");
  }
}
