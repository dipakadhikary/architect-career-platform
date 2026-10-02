package com.acos.learning.event;

import com.acos.common.event.AfterCommitEventPublisher;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Publishes learning domain events after successful transaction commit. */
@Component
public class LearningDomainEventPublisher {

  private final AfterCommitEventPublisher afterCommitEventPublisher;

  /**
   * Creates the publisher.
   *
   * @param afterCommitEventPublisher shared after-commit publisher
   */
  public LearningDomainEventPublisher(AfterCommitEventPublisher afterCommitEventPublisher) {
    this.afterCommitEventPublisher =
        Objects.requireNonNull(
            afterCommitEventPublisher, "afterCommitEventPublisher must not be null");
  }

  /**
   * Publishes a learning-plan-completed event after commit.
   *
   * @param event completed event
   */
  public void publish(LearningPlanCompletedEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    afterCommitEventPublisher.publish(event);
  }
}
