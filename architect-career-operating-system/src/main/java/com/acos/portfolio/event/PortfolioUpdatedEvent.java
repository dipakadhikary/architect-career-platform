package com.acos.portfolio.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published when a portfolio project is created or updated.
 *
 * @param projectId project id
 * @param ownerId owning user id
 * @param title project title
 * @param occurredAt event time
 */
public record PortfolioUpdatedEvent(
    UUID projectId, UUID ownerId, String title, Instant occurredAt) {

  /**
   * Validates required fields.
   *
   * @param projectId project id
   * @param ownerId owner id
   * @param title title
   * @param occurredAt occurred at
   */
  public PortfolioUpdatedEvent {
    Objects.requireNonNull(projectId, "projectId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
