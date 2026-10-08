package com.acos.notification.api;

import java.util.Map;

/**
 * Request to render a stored template and deliver it.
 *
 * @param channel email or SMS
 * @param templateCode template code, such as {@code PASSWORD_RESET}
 * @param recipient destination address or phone number
 * @param variables placeholder values; keys match {@code {{name}}} in the template
 */
public record NotificationMessage(
    NotificationChannel channel,
    String templateCode,
    String recipient,
    Map<String, String> variables) {}
