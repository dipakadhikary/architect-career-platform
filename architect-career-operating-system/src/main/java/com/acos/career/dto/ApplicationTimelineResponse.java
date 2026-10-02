package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

/**
 * Ordered timeline milestones for a single job application, used for visualization.
 *
 * @param steps ordered milestone steps
 */
@Schema(name = "ApplicationTimelineResponse", description = "Application timeline visualization")
public record ApplicationTimelineResponse(
    @Schema(description = "Ordered milestone steps", requiredMode = Schema.RequiredMode.REQUIRED)
        List<TimelineStepResponse> steps) {

  /**
   * Creates an immutable timeline response.
   *
   * @param steps ordered milestone steps
   */
  public ApplicationTimelineResponse {
    Objects.requireNonNull(steps, "steps must not be null");
    steps = List.copyOf(steps);
  }
}
