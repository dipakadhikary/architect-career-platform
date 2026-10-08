package com.acos.auth.mail;

/**
 * Sends account-recovery messages through the notification module. The reset URL is printed only
 * when the configured email provider cannot deliver.
 */
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
