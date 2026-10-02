package com.acos.career.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published when a new offer is recorded for a job application.
 *
 * @param offerId offer id
 * @param applicationId owning application id
 * @param ownerId owning user id
 * @param occurredAt instant the offer was recorded
 */
public record OfferReceivedEvent(UUID offerId, UUID applicationId, UUID ownerId, Instant occurredAt)
    implements CareerDomainEvent {

  /**
   * Creates the event, validating required fields.
   *
   * @param offerId offer id
   * @param applicationId owning application id
   * @param ownerId owning user id
   * @param occurredAt occurred-at instant
   */
  public OfferReceivedEvent {
    Objects.requireNonNull(offerId, "offerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
