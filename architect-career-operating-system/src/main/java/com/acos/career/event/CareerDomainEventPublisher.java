package com.acos.career.event;

import com.acos.common.event.AfterCommitEventPublisher;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Publishes {@link CareerDomainEvent}s only after the enclosing transaction commits successfully.
 * When no transaction is active, events are published immediately.
 */
@Component
public class CareerDomainEventPublisher {

  private final AfterCommitEventPublisher afterCommitEventPublisher;

  /**
   * Creates the publisher.
   *
   * @param afterCommitEventPublisher shared after-commit publisher
   */
  public CareerDomainEventPublisher(AfterCommitEventPublisher afterCommitEventPublisher) {
    this.afterCommitEventPublisher =
        Objects.requireNonNull(
            afterCommitEventPublisher, "afterCommitEventPublisher must not be null");
  }

  /**
   * Publishes a domain event after the current transaction commits, or immediately when no
   * transaction is active.
   *
   * @param event domain event to publish
   */
  public void publish(CareerDomainEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    afterCommitEventPublisher.publish(event);
  }
}
