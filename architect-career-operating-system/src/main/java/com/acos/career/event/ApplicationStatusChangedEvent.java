package com.acos.career.event;

import com.acos.career.entity.ApplicationStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published on every job application status transition.
 *
 * @param applicationId application id
 * @param ownerId owning user id
 * @param oldStatus previous status
 * @param newStatus resulting status
 * @param occurredAt instant the transition occurred
 */
public record ApplicationStatusChangedEvent(
    UUID applicationId,
    UUID ownerId,
    ApplicationStatus oldStatus,
    ApplicationStatus newStatus,
    Instant occurredAt)
    implements CareerDomainEvent {

  /**
   * Creates the event, validating required fields.
   *
   * @param applicationId application id
   * @param ownerId owning user id
   * @param oldStatus previous status
   * @param newStatus resulting status
   * @param occurredAt occurred-at instant
   */
  public ApplicationStatusChangedEvent {
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(oldStatus, "oldStatus must not be null");
    Objects.requireNonNull(newStatus, "newStatus must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
