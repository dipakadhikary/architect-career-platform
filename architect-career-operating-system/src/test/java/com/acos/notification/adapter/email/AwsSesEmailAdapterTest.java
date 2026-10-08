package com.acos.notification.adapter.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.RenderedNotification;
import com.acos.notification.config.NotificationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

/** SES adapter sends the rendered design and does not log it. */
@ExtendWith(MockitoExtension.class)
class AwsSesEmailAdapterTest {

  @Mock private SesV2Client client;

  @Test
  void shouldSendTheRenderedEmail() {
    NotificationProperties properties =
        new NotificationProperties(
            new NotificationProperties.Email("aws", "noreply@acos.local", "us-east-1"),
            new NotificationProperties.Sms("console"));
    AwsSesEmailAdapter adapter = new AwsSesEmailAdapter(client, properties);
    RenderedNotification notification =
        new RenderedNotification(
            NotificationChannel.EMAIL,
            "ada@acos.local",
            "PASSWORD_RESET",
            "Reset",
            "<a href=\"http://localhost/reset-password?token=raw-secret\">Reset</a>",
            "http://localhost/reset-password?token=raw-secret");

    assertThat(adapter.available()).isTrue();
    adapter.deliver(notification);

    ArgumentCaptor<SendEmailRequest> request = ArgumentCaptor.forClass(SendEmailRequest.class);
    verify(client).sendEmail(request.capture());
    assertThat(request.getValue().fromEmailAddress()).isEqualTo("noreply@acos.local");
    assertThat(request.getValue().destination().toAddresses()).containsExactly("ada@acos.local");
    assertThat(request.getValue().content().simple().body().html().data()).contains("raw-secret");
  }

  @Test
  void shouldBeUnavailableWithoutAFromAddress() {
    NotificationProperties properties =
        new NotificationProperties(
            new NotificationProperties.Email("aws", "", "us-east-1"),
            new NotificationProperties.Sms("console"));
    AwsSesEmailAdapter adapter = new AwsSesEmailAdapter(client, properties);

    assertThat(adapter.available()).isFalse();
  }
}
