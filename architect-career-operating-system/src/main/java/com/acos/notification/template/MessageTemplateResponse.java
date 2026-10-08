package com.acos.notification.template;

import com.acos.notification.api.NotificationChannel;
import java.util.UUID;

/**
 * Template shown in the designer.
 *
 * @param id identifier
 * @param code stable code
 * @param channel email or SMS
 * @param name display name
 * @param description description
 * @param subject email subject
 * @param htmlBody HTML design
 * @param textBody plain text or SMS body
 * @param sampleData sample placeholder JSON
 * @param enabled whether delivery may use the template
 * @param version optimistic lock version
 */
public record MessageTemplateResponse(
    UUID id,
    String code,
    NotificationChannel channel,
    String name,
    String description,
    String subject,
    String htmlBody,
    String textBody,
    String sampleData,
    boolean enabled,
    long version) {

  /**
   * Maps a stored template.
   *
   * @param template entity
   * @return response
   */
  public static MessageTemplateResponse from(MessageTemplate template) {
    return new MessageTemplateResponse(
        template.getId(),
        template.getCode(),
        template.getChannel(),
        template.getName(),
        template.getDescription(),
        template.getSubject(),
        template.getHtmlBody(),
        template.getTextBody(),
        template.getSampleData(),
        template.isEnabled(),
        template.getVersion());
  }
}
