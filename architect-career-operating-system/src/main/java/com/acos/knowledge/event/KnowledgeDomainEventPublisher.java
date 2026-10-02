package com.acos.knowledge.event;

import com.acos.common.event.AfterCommitEventPublisher;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Publishes knowledge domain events after successful transaction commit. */
@Component
public class KnowledgeDomainEventPublisher {

  private final AfterCommitEventPublisher afterCommitEventPublisher;

  /**
   * Creates the publisher.
   *
   * @param afterCommitEventPublisher shared after-commit publisher
   */
  public KnowledgeDomainEventPublisher(AfterCommitEventPublisher afterCommitEventPublisher) {
    this.afterCommitEventPublisher =
        Objects.requireNonNull(
            afterCommitEventPublisher, "afterCommitEventPublisher must not be null");
  }

  /**
   * Publishes a knowledge created event after commit.
   *
   * @param event created event
   */
  public void publish(KnowledgeCreatedEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    afterCommitEventPublisher.publish(event);
  }

  /**
   * Publishes a knowledge updated event after commit.
   *
   * @param event updated event
   */
  public void publish(KnowledgeUpdatedEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    afterCommitEventPublisher.publish(event);
  }
}
