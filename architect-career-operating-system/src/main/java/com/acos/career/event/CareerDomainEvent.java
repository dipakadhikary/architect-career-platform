package com.acos.career.event;

import java.time.Instant;

/** Marker for career tracker domain events published after successful state changes. */
public interface CareerDomainEvent {

  /**
   * Returns the instant the underlying business event occurred.
   *
   * @return occurred-at instant
   */
  Instant occurredAt();
}
