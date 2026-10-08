package com.acos.notification.dispatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.acos.notification.adapter.ConsoleNotificationAdapter;
import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.NotificationMessage;
import com.acos.notification.api.RenderedNotification;
import com.acos.notification.config.NotificationProperties;
import com.acos.notification.template.MessageTemplate;
import com.acos.notification.template.MessageTemplateRepository;
import com.acos.notification.template.TemplateRenderer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

/** Provider selection and console fallback. */
@ExtendWith(MockitoExtension.class)
class NotificationDispatcherTest {

  private static final String RESET_URL = "http://localhost:5173/reset-password?token=raw-secret";

  @Mock private MessageTemplateRepository templateRepository;

  @Test
  void shouldPrintTheResetUrlWhenTheEmailProviderIsUnavailable() {
    RecordingAdapter aws = new RecordingAdapter(false, null);
    NotificationDispatcher dispatcher = dispatcher("aws", "from@acos.local", aws);

    ListAppender<ILoggingEvent> logs = attachConsoleLog();
    try {
      dispatcher.deliver(resetMessage());
      assertThat(aws.sent).isFalse();
      assertThat(logs.list)
          .anySatisfy(event -> assertThat(event.getFormattedMessage()).contains(RESET_URL));
    } finally {
      detach(logs);
    }
  }

  @Test
  void shouldPrintTheResetUrlWhenTheEmailProviderFails() {
    RecordingAdapter aws = new RecordingAdapter(true, new IllegalStateException("ses down"));
    NotificationDispatcher dispatcher = dispatcher("aws", "from@acos.local", aws);

    ListAppender<ILoggingEvent> logs = attachConsoleLog();
    try {
      dispatcher.deliver(resetMessage());
      assertThat(logs.list)
          .anySatisfy(event -> assertThat(event.getFormattedMessage()).contains(RESET_URL));
      assertThat(logs.list)
          .allSatisfy(
              event ->
                  assertThat(event.getLoggerName())
                      .isNotEqualTo(
                          "com.acos.notification.adapter.email.AwsSesEmailAdapter"));
    } finally {
      detach(logs);
    }
  }

  @Test
  void shouldNotPrintTheResetUrlWhenAwsAcceptsTheMessage() {
    RecordingAdapter aws = new RecordingAdapter(true, null);
    NotificationDispatcher dispatcher = dispatcher("aws", "from@acos.local", aws);

    ListAppender<ILoggingEvent> logs = attachConsoleLog();
    try {
      dispatcher.deliver(resetMessage());
      assertThat(aws.sent).isTrue();
      assertThat(aws.last.textBody()).contains(RESET_URL);
      assertThat(logs.list).isEmpty();
    } finally {
      detach(logs);
    }
  }

  private NotificationDispatcher dispatcher(String provider, String from, ChannelAdapter aws) {
    MessageTemplate template =
        new MessageTemplate(
            "PASSWORD_RESET",
            NotificationChannel.EMAIL,
            "Reset",
            "Reset your password:\n{{resetUrl}}",
            "{}");
    template.setSubject("ACOS - Reset your password");
    template.setHtmlBody("<a href=\"{{resetUrl}}\">Reset password</a>");
    when(templateRepository.findByCodeAndChannel("PASSWORD_RESET", NotificationChannel.EMAIL))
        .thenReturn(Optional.of(template));
    NotificationProperties properties =
        new NotificationProperties(
            new NotificationProperties.Email(provider, from, "us-east-1"),
            new NotificationProperties.Sms("console"));
    return new NotificationDispatcher(
        templateRepository,
        new TemplateRenderer(),
        properties,
        List.of(aws, new ConsoleNotificationAdapter()),
        new ConsoleNotificationAdapter());
  }

  private static NotificationMessage resetMessage() {
    return new NotificationMessage(
        NotificationChannel.EMAIL, "PASSWORD_RESET", "ada@acos.local", Map.of("resetUrl", RESET_URL));
  }

  private static ListAppender<ILoggingEvent> attachConsoleLog() {
    Logger logger = (Logger) LoggerFactory.getLogger(ConsoleNotificationAdapter.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
    return appender;
  }

  private static void detach(ListAppender<ILoggingEvent> appender) {
    Logger logger = (Logger) LoggerFactory.getLogger(ConsoleNotificationAdapter.class);
    logger.detachAppender(appender);
  }

  private static final class RecordingAdapter implements ChannelAdapter {
    private final boolean available;
    private final RuntimeException failure;
    private boolean sent;
    private RenderedNotification last;

    private RecordingAdapter(boolean available, RuntimeException failure) {
      this.available = available;
      this.failure = failure;
    }

    @Override
    public boolean supports(NotificationChannel channel, String provider) {
      return channel == NotificationChannel.EMAIL && "aws".equals(provider);
    }

    @Override
    public boolean available() {
      return available;
    }

    @Override
    public void deliver(RenderedNotification notification) {
      if (failure != null) {
        throw failure;
      }
      sent = true;
      last = notification;
    }
  }
}
