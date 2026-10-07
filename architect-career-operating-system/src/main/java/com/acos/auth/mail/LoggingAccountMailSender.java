package com.acos.auth.mail;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Records that a recovery email was accepted. The platform has no SMTP integration, so this sender
 * does not transmit the message and never logs the login identifier or reset URL.
 */
@Component
public class LoggingAccountMailSender implements AccountMailSender {

  private static final Logger LOG = LoggerFactory.getLogger(LoggingAccountMailSender.class);

  @Override
  public void sendLoginIdentifier(String recipient, String loginIdentifier) {
    Objects.requireNonNull(recipient, "recipient must not be null");
    Objects.requireNonNull(loginIdentifier, "loginIdentifier must not be null");
    LOG.info("Login identifier recovery email accepted");
  }

  @Override
  public void sendPasswordReset(String recipient, String resetUrl) {
    Objects.requireNonNull(recipient, "recipient must not be null");
    Objects.requireNonNull(resetUrl, "resetUrl must not be null");
    LOG.info("Password reset email accepted");
  }
}
