package com.acos.common.event;

import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Publishes Spring application events only after the enclosing transaction commits successfully.
 * When no transaction is active, events are published immediately.
 */
@Component
public class AfterCommitEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Creates the publisher.
   *
   * @param applicationEventPublisher underlying Spring event publisher
   */
  public AfterCommitEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher =
        Objects.requireNonNull(
            applicationEventPublisher, "applicationEventPublisher must not be null");
  }

  /**
   * Publishes an event after the current transaction commits, or immediately when no transaction is
   * active.
   *
   * @param event event payload
   */
  public void publish(Object event) {
    Objects.requireNonNull(event, "event must not be null");
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              applicationEventPublisher.publishEvent(event);
            }
          });
    } else {
      applicationEventPublisher.publishEvent(event);
    }
  }
}
