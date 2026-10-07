package com.acos.auth.mail;

/** Sends account-recovery messages. Implementations must not log message bodies. */
public interface AccountMailSender {

  /**
   * Sends the login identifier to the account email.
   *
   * @param recipient account email
   * @param loginIdentifier identifier used at login
   */
  void sendLoginIdentifier(String recipient, String loginIdentifier);

  /**
   * Sends a password reset link.
   *
   * @param recipient account email
   * @param resetUrl absolute URL on the configured frontend origin
   */
  void sendPasswordReset(String recipient, String resetUrl);
}
