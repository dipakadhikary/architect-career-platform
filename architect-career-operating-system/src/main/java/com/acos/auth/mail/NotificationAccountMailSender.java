package com.acos.auth.mail;

import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.NotificationMessage;
import com.acos.notification.api.NotificationPort;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Sends account recovery through the notification module. */
@Component
public class NotificationAccountMailSender implements AccountMailSender {

  private final NotificationPort notifications;

  /**
   * Creates the sender.
   *
   * @param notifications notification port
   */
  public NotificationAccountMailSender(NotificationPort notifications) {
    this.notifications = notifications;
  }

  @Override
  public void sendLoginIdentifier(String recipient, String loginIdentifier) {
    notifications.deliver(
        new NotificationMessage(
            NotificationChannel.EMAIL,
            "LOGIN_IDENTIFIER",
            recipient,
            Map.of("loginIdentifier", loginIdentifier)));
  }

  @Override
  public void sendPasswordReset(String recipient, String resetUrl) {
    notifications.deliver(
        new NotificationMessage(
            NotificationChannel.EMAIL,
            "PASSWORD_RESET",
            recipient,
            Map.of("resetUrl", resetUrl)));
  }
}
