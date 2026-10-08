package com.acos.notification.api;

/** Delivery channel. Add a {@code ChannelAdapter} to support a new provider for a channel. */
public enum NotificationChannel {
  /** HTML and text email. */
  EMAIL,

  /** Plain-text message. */
  SMS
}
