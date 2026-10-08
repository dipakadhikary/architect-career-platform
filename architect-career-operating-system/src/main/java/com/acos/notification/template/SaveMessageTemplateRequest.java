package com.acos.notification.template;

import com.acos.notification.api.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Create or update payload for a message template.
 *
 * @param code stable code, required on create
 * @param channel channel, required on create
 * @param name display name
 * @param description description
 * @param subject email subject
 * @param htmlBody HTML design
 * @param textBody plain text or SMS body
 * @param sampleData sample placeholder JSON
 * @param enabled enabled flag
 * @param version current version, required on update
 */
public record SaveMessageTemplateRequest(
    @Size(max = 64) String code,
    NotificationChannel channel,
    @NotBlank @Size(max = 120) String name,
    @Size(max = 500) String description,
    @Size(max = 200) String subject,
    @Size(max = 100000) String htmlBody,
    @NotBlank @Size(max = 10000) String textBody,
    @NotBlank @Size(max = 4000) String sampleData,
    @NotNull Boolean enabled,
    Long version) {}
