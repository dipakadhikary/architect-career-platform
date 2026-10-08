package com.acos.notification.adapter.email;

import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.RenderedNotification;
import com.acos.notification.config.NotificationProperties;
import com.acos.notification.dispatch.ChannelAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

/** Sends email through Amazon SES. The message body is not written to the log. */
@Component
@ConditionalOnProperty(prefix = "acos.notification.email", name = "provider", havingValue = "aws")
public class AwsSesEmailAdapter implements ChannelAdapter {

  private final SesV2Client client;
  private final NotificationProperties properties;

  /**
   * Creates the SES adapter.
   *
   * @param client SES client
   * @param properties sender address
   */
  public AwsSesEmailAdapter(SesV2Client client, NotificationProperties properties) {
    this.client = client;
    this.properties = properties;
  }

  @Override
  public boolean supports(NotificationChannel channel, String provider) {
    return channel == NotificationChannel.EMAIL && "aws".equalsIgnoreCase(provider);
  }

  @Override
  public boolean available() {
    return !properties.email().fromAddress().isBlank();
  }

  @Override
  public void deliver(RenderedNotification notification) {
    Content subject = content(notification.subject());
    Body body =
        Body.builder()
            .text(content(notification.textBody()))
            .html(content(notification.htmlBody()))
            .build();
    SendEmailRequest request =
        SendEmailRequest.builder()
            .fromEmailAddress(properties.email().fromAddress())
            .destination(Destination.builder().toAddresses(notification.recipient()).build())
            .content(
                EmailContent.builder()
                    .simple(Message.builder().subject(subject).body(body).build())
                    .build())
            .build();
    client.sendEmail(request);
  }

  private static Content content(String value) {
    return Content.builder().data(value == null ? "" : value).charset("UTF-8").build();
  }
}
