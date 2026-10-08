package com.acos.notification.adapter;

import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.RenderedNotification;
import com.acos.notification.dispatch.ChannelAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Prints the rendered message, including a reset URL, when the selected email or SMS provider is
 * not available.
 */
@Component
public class ConsoleNotificationAdapter implements ChannelAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ConsoleNotificationAdapter.class);

  @Override
  public boolean supports(NotificationChannel channel, String provider) {
    return "console".equalsIgnoreCase(provider);
  }

  @Override
  public boolean available() {
    return true;
  }

  @Override
  public void deliver(RenderedNotification notification) {
    LOG.info(
        """
        Notification console delivery
        channel: {}
        recipient: {}
        subject: {}
        {}
        """,
        notification.channel(),
        notification.recipient(),
        notification.subject(),
        notification.textBody());
  }
}
