package com.acos.notification.api;

/**
 * Template after placeholder substitution.
 *
 * @param channel delivery channel
 * @param recipient destination
 * @param templateCode template code
 * @param subject email subject, empty for SMS
 * @param htmlBody email HTML, empty for SMS
 * @param textBody plain text, also used for SMS
 */
public record RenderedNotification(
    NotificationChannel channel,
    String recipient,
    String templateCode,
    String subject,
    String htmlBody,
    String textBody) {}
