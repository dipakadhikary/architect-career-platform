package com.acos.notification.template;

/**
 * Rendered template for the designer preview.
 *
 * @param subject rendered subject
 * @param htmlBody rendered HTML design
 * @param textBody rendered plain text
 */
public record TemplatePreviewResponse(String subject, String htmlBody, String textBody) {}
