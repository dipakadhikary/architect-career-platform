package com.acos.tutorial.event;

import com.acos.common.event.AfterCommitEventPublisher;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Publishes tutorial index events after the business transaction commits. */
@Component
public class TutorialIndexPublisher {

  private final AfterCommitEventPublisher afterCommitEventPublisher;

  /**
   * Creates the publisher.
   *
   * @param afterCommitEventPublisher shared after-commit publisher
   */
  public TutorialIndexPublisher(AfterCommitEventPublisher afterCommitEventPublisher) {
    this.afterCommitEventPublisher =
        Objects.requireNonNull(
            afterCommitEventPublisher, "afterCommitEventPublisher must not be null");
  }

  /**
   * Publishes a tutorial content change after commit.
   *
   * @param event content event
   */
  public void publish(TutorialContentEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    afterCommitEventPublisher.publish(event);
  }
}
