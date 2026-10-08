package com.acos.notification.dispatch;

import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.RenderedNotification;

/**
 * Provider for one channel. Register another bean to switch from AWS email to a different service,
 * or to add an SMS provider, without changing callers.
 */
public interface ChannelAdapter {

  /**
   * Returns whether this adapter handles the channel and provider id.
   *
   * @param channel email or SMS
   * @param provider configured provider name
   * @return {@code true} when this adapter should be selected
   */
  boolean supports(NotificationChannel channel, String provider);

  /**
   * Returns whether the provider can accept a message right now.
   *
   * @return {@code true} when delivery should be attempted
   */
  boolean available();

  /**
   * Sends a rendered message.
   *
   * @param notification rendered template
   */
  void deliver(RenderedNotification notification);
}
