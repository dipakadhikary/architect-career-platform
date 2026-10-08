package com.acos.notification.api;

/** Port other modules use to send a templated notification. */
public interface NotificationPort {

  /**
   * Renders the template and delivers it through the configured provider.
   *
   * @param message channel, template, recipient, and variables
   */
  void deliver(NotificationMessage message);
}
