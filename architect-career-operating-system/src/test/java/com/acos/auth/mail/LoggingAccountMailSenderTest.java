package com.acos.auth.mail;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/** Confirms recovery mail logs omit the identifier and the reset link. */
class LoggingAccountMailSenderTest {

  @Test
  void shouldNotLogTheResetTokenOrLoginIdentifier() {
    Logger logger = (Logger) LoggerFactory.getLogger(LoggingAccountMailSender.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
    try {
      LoggingAccountMailSender sender = new LoggingAccountMailSender();
      sender.sendLoginIdentifier("ada@acos.local", "ada@acos.local");
      sender.sendPasswordReset(
          "ada@acos.local", "http://localhost:5173/reset-password?token=raw-secret");

      assertThat(appender.list).isNotEmpty();
      assertThat(appender.list)
          .allSatisfy(
              event ->
                  assertThat(event.getFormattedMessage())
                      .doesNotContain("ada@acos.local")
                      .doesNotContain("raw-secret")
                      .doesNotContain("reset-password"));
    } finally {
      logger.detachAppender(appender);
    }
  }
}
