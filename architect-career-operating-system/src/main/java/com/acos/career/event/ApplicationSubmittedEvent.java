package com.acos.career.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published when a job application transitions into {@code APPLIED}.
 *
 * @param applicationId application id
 * @param ownerId owning user id
 * @param occurredAt instant the transition occurred
 */
public record ApplicationSubmittedEvent(UUID applicationId, UUID ownerId, Instant occurredAt)
    implements CareerDomainEvent {

  /**
   * Creates the event, validating required fields.
   *
   * @param applicationId application id
   * @param ownerId owning user id
   * @param occurredAt occurred-at instant
   */
  public ApplicationSubmittedEvent {
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
