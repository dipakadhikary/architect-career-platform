package com.acos.portfolio.event;

import com.acos.common.event.AfterCommitEventPublisher;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Publishes portfolio domain events after successful transaction commit. */
@Component
public class PortfolioDomainEventPublisher {

  private final AfterCommitEventPublisher afterCommitEventPublisher;

  /**
   * Creates the publisher.
   *
   * @param afterCommitEventPublisher shared after-commit publisher
   */
  public PortfolioDomainEventPublisher(AfterCommitEventPublisher afterCommitEventPublisher) {
    this.afterCommitEventPublisher =
        Objects.requireNonNull(
            afterCommitEventPublisher, "afterCommitEventPublisher must not be null");
  }

  /**
   * Publishes a portfolio-updated event after commit.
   *
   * @param event updated event
   */
  public void publish(PortfolioUpdatedEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    afterCommitEventPublisher.publish(event);
  }
}
