package com.acos.learning.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published when a learning plan transitions to completed.
 *
 * @param planId learning plan id
 * @param ownerId owning user id
 * @param occurredAt event time
 */
public record LearningPlanCompletedEvent(UUID planId, UUID ownerId, Instant occurredAt) {

  /**
   * Validates required fields.
   *
   * @param planId plan id
   * @param ownerId owner id
   * @param occurredAt occurred at
   */
  public LearningPlanCompletedEvent {
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
