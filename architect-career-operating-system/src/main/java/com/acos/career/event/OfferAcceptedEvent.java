package com.acos.career.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published when an offer is accepted.
 *
 * @param offerId offer id
 * @param applicationId owning application id
 * @param ownerId owning user id
 * @param occurredAt instant the offer was accepted
 */
public record OfferAcceptedEvent(UUID offerId, UUID applicationId, UUID ownerId, Instant occurredAt)
    implements CareerDomainEvent {

  /**
   * Creates the event, validating required fields.
   *
   * @param offerId offer id
   * @param applicationId owning application id
   * @param ownerId owning user id
   * @param occurredAt occurred-at instant
   */
  public OfferAcceptedEvent {
    Objects.requireNonNull(offerId, "offerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
  }
}
