package com.acos.career.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published when a new interview is scheduled.
 *
 * @param interviewId interview id
 * @param applicationId owning application id
 * @param ownerId owning user id
 * @param occurredAt instant the interview was scheduled
 */
public record InterviewScheduledEvent(
    UUID interviewId, UUID applicationId, UUID ownerId, Instant occurredAt)
    implements CareerDomainEvent {

  /**
   * Creates the event, validating required fields.
   *
   * @param interviewId interview id
   * @param applicationId owning application id
   * @param ownerId owning user id
   * @param occurredAt occurred-at instant
   */
  public InterviewScheduledEvent {
    Objects.requireNonNull(interviewId, "interviewId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
