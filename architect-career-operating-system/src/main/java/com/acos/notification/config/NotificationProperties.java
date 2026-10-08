package com.acos.notification.config;

import com.acos.notification.api.NotificationChannel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Provider selection for the notification module.
 *
 * @param email email provider settings
 * @param sms SMS provider settings
 */
@Validated
@ConfigurationProperties(prefix = "acos.notification")
public record NotificationProperties(Email email, Sms sms) {

  /**
   * Applies local defaults when a section is omitted.
   *
   * @param email email settings
   * @param sms SMS settings
   */
  public NotificationProperties {
    if (email == null) {
      email = new Email("aws", "", "us-east-1");
    }
    if (sms == null) {
      sms = new Sms("console");
    }
  }

  /**
   * Returns the configured provider name for a channel.
   *
   * @param channel email or SMS
   * @return provider id, such as {@code aws} or {@code console}
   */
  public String providerFor(NotificationChannel channel) {
    if (channel == NotificationChannel.SMS) {
      return sms.provider();
    }
    return email.provider();
  }

  /**
   * Email provider settings.
   *
   * @param provider {@code aws} or {@code console}
   * @param fromAddress verified SES sender; blank keeps delivery on the console
   * @param region AWS region
   */
  public record Email(String provider, String fromAddress, String region) {

    /**
     * Fills blank provider and region.
     *
     * @param provider provider id
     * @param fromAddress sender address
     * @param region AWS region
     */
    public Email {
      if (provider == null || provider.isBlank()) {
        provider = "aws";
      }
      if (fromAddress == null) {
        fromAddress = "";
      }
      if (region == null || region.isBlank()) {
        region = "us-east-1";
      }
    }
  }

  /**
   * SMS provider settings.
   *
   * @param provider {@code console} until another SMS adapter is registered
   */
  public record Sms(String provider) {

    /**
     * Fills a blank provider.
     *
     * @param provider provider id
     */
    public Sms {
      if (provider == null || provider.isBlank()) {
        provider = "console";
      }
    }
  }
}
