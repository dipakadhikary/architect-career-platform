package com.acos.notification.template;

import com.acos.common.persistence.BaseEntity;
import com.acos.notification.api.NotificationChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.util.Objects;

/** Database template for one channel and code. */
@Entity
@Table(
    name = "message_templates",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_message_templates_code_channel",
            columnNames = {"code", "channel"}))
public class MessageTemplate extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "code", nullable = false, length = 64, updatable = false)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "channel", nullable = false, length = 16, updatable = false)
  private NotificationChannel channel;

  @Column(name = "name", nullable = false, length = 120)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "subject", length = 200)
  private String subject;

  @Column(name = "html_body")
  private String htmlBody;

  @Column(name = "text_body", nullable = false)
  private String textBody;

  @Column(name = "sample_data", nullable = false)
  private String sampleData;

  @Column(name = "enabled", nullable = false)
  private boolean enabled = true;

  /** Creates an empty template for JPA. */
  protected MessageTemplate() {}

  /**
   * Creates a template.
   *
   * @param code stable code
   * @param channel delivery channel
   * @param name display name
   * @param textBody plain text or SMS body
   * @param sampleData JSON object of sample placeholders
   */
  public MessageTemplate(
      String code, NotificationChannel channel, String name, String textBody, String sampleData) {
    this.code = Objects.requireNonNull(code, "code must not be null");
    this.channel = Objects.requireNonNull(channel, "channel must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.textBody = Objects.requireNonNull(textBody, "textBody must not be null");
    this.sampleData = Objects.requireNonNull(sampleData, "sampleData must not be null");
  }

  /**
   * Returns the template code.
   *
   * @return code
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the channel.
   *
   * @return channel
   */
  public NotificationChannel getChannel() {
    return channel;
  }

  /**
   * Returns the display name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Updates the display name.
   *
   * @param name name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the description.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Updates the description.
   *
   * @param description description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the email subject.
   *
   * @return subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Updates the email subject.
   *
   * @param subject subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Returns the HTML design.
   *
   * @return HTML body
   */
  public String getHtmlBody() {
    return htmlBody;
  }

  /**
   * Updates the HTML design.
   *
   * @param htmlBody HTML body
   */
  public void setHtmlBody(String htmlBody) {
    this.htmlBody = htmlBody;
  }

  /**
   * Returns the plain-text or SMS body.
   *
   * @return text body
   */
  public String getTextBody() {
    return textBody;
  }

  /**
   * Updates the plain-text or SMS body.
   *
   * @param textBody text body
   */
  public void setTextBody(String textBody) {
    this.textBody = Objects.requireNonNull(textBody, "textBody must not be null");
  }

  /**
   * Returns sample placeholder JSON.
   *
   * @return sample data
   */
  public String getSampleData() {
    return sampleData;
  }

  /**
   * Updates sample placeholder JSON.
   *
   * @param sampleData sample data
   */
  public void setSampleData(String sampleData) {
    this.sampleData = Objects.requireNonNull(sampleData, "sampleData must not be null");
  }

  /**
   * Returns whether delivery may use this template.
   *
   * @return enabled flag
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Updates the enabled flag.
   *
   * @param enabled enabled flag
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
