package com.acos.auth.recovery;

import com.acos.auth.dto.ResetPasswordRequest;
import com.acos.auth.entity.PasswordResetToken;
import com.acos.auth.entity.User;
import com.acos.auth.exception.InvalidPasswordResetTokenException;
import com.acos.auth.exception.WeakPasswordException;
import com.acos.auth.mail.AccountMailSender;
import com.acos.auth.mapper.UserMapper;
import com.acos.auth.repository.PasswordResetTokenRepository;
import com.acos.auth.repository.UserRepository;
import com.acos.auth.token.TokenService;
import com.acos.auth.validator.PasswordValidator;
import com.acos.common.api.ApiError;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Login-identifier recovery and password reset. Login uses the account email, so identifier
 * recovery emails that address and does not invent a separate username. Public methods return the
 * same acknowledgement for known and unknown emails. A newer reset request supersedes unused
 * tokens. Access tokens stay valid until they expire because they are stateless; refresh tokens
 * are revoked.
 */
@Service
public class AccountRecoveryService {

  private static final Logger LOG = LoggerFactory.getLogger(AccountRecoveryService.class);
  private static final int TOKEN_BYTES = 32;

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final PasswordValidator passwordValidator;
  private final TokenService tokenService;
  private final AccountMailSender mailSender;
  private final AccountRecoveryProperties properties;
  private final RecoveryRateLimiter rateLimiter;
  private final Clock clock;
  private final SecureRandom secureRandom;
  private final Counter loginRecoveryRequests;
  private final Counter passwordResetRequests;
  private final Counter passwordResetSuccess;
  private final Counter passwordResetFailure;
  private final Counter passwordResetEmailFailures;

  /**
   * Creates the recovery service.
   *
   * @param userRepository user persistence
   * @param tokenRepository reset-token persistence
   * @param userMapper email normalization
   * @param passwordEncoder existing password hasher
   * @param passwordValidator existing password policy
   * @param tokenService refresh-token revocation
   * @param mailSender recovery email sender
   * @param properties recovery settings
   * @param rateLimiter public-endpoint limiter
   * @param clock time source
   * @param meterRegistry metrics registry
   */
  public AccountRecoveryService(
      UserRepository userRepository,
      PasswordResetTokenRepository tokenRepository,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      PasswordValidator passwordValidator,
      TokenService tokenService,
      AccountMailSender mailSender,
      AccountRecoveryProperties properties,
      RecoveryRateLimiter rateLimiter,
      Clock clock,
      MeterRegistry meterRegistry) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.passwordValidator = passwordValidator;
    this.tokenService = tokenService;
    this.mailSender = mailSender;
    this.properties = properties;
    this.rateLimiter = rateLimiter;
    this.clock = clock;
    this.secureRandom = new SecureRandom();
    this.loginRecoveryRequests = counter(meterRegistry, "user_id_recovery_requests_total");
    this.passwordResetRequests = counter(meterRegistry, "password_reset_requests_total");
    this.passwordResetSuccess = counter(meterRegistry, "password_reset_success_total");
    this.passwordResetFailure = counter(meterRegistry, "password_reset_failure_total");
    this.passwordResetEmailFailures = counter(meterRegistry, "password_reset_email_failures_total");
  }

  /**
   * Emails the login identifier when the account exists and is enabled.
   *
   * @param email submitted email
   * @return generic acknowledgement
   */
  @Transactional
  public RecoveryAcknowledgement requestLoginIdentifier(String email) {
    loginRecoveryRequests.increment();
    String normalized = userMapper.normalizeEmail(email);
    if (!rateLimiter.allow("login:" + normalized)) {
      LOG.info("Login identifier recovery rate limited");
      return RecoveryAcknowledgement.generic();
    }
    Optional<User> found = userRepository.findByEmail(normalized);
    if (found.isPresent() && found.get().isEnabled()) {
      User user = found.get();
      LOG.info("Login identifier recovery requested for user {}", user.getId());
      sendQuietly(() -> mailSender.sendLoginIdentifier(user.getEmail(), user.getEmail()), true);
    }
    return RecoveryAcknowledgement.generic();
  }

  /**
   * Stores a hashed reset token and emails the raw token when the account exists and is enabled.
   *
   * @param email submitted email
   * @return generic acknowledgement
   */
  @Transactional
  public RecoveryAcknowledgement requestPasswordReset(String email) {
    passwordResetRequests.increment();
    String normalized = userMapper.normalizeEmail(email);
    if (!rateLimiter.allow("reset:" + normalized)) {
      LOG.info("Password reset request rate limited");
      return RecoveryAcknowledgement.generic();
    }
    Optional<User> found = userRepository.findByEmail(normalized);
    if (found.isPresent() && found.get().isEnabled()) {
      User user = found.get();
      Instant now = Instant.now(clock);
      tokenRepository
          .findUnusedByUserId(user.getId())
          .forEach(token -> token.supersede(now));
      String rawToken = newRawToken();
      Instant expiresAt = now.plus(properties.tokenTtl());
      tokenRepository.save(new PasswordResetToken(user, sha256(rawToken), expiresAt));
      LOG.info("Password reset requested for user {}", user.getId());
      sendQuietly(() -> mailSender.sendPasswordReset(user.getEmail(), resetUrl(rawToken)), false);
    }
    return RecoveryAcknowledgement.generic();
  }

  /**
   * Replaces the password when the reset token is unused and unexpired.
   *
   * @param request new password and raw token
   */
  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new WeakPasswordException(
          "Passwords do not match",
          java.util.List.of(
              ApiError.FieldErrorDetail.ofField("confirmPassword", "must match newPassword")));
    }
    passwordValidator.validate(request.newPassword());

    PasswordResetToken token =
        tokenRepository
            .findByTokenHashForUpdate(sha256(request.token()))
            .orElseThrow(InvalidPasswordResetTokenException::new);
    Instant now = Instant.now(clock);
    if (!token.consume(now)) {
      passwordResetFailure.increment();
      LOG.info("Password reset rejected");
      throw new InvalidPasswordResetTokenException();
    }
    User user = token.getUser();
    if (!user.isEnabled()) {
      passwordResetFailure.increment();
      throw new InvalidPasswordResetTokenException();
    }
    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);
    tokenService.revokeAllRefreshTokens(user.getId());
    passwordResetSuccess.increment();
    LOG.info("Password reset completed for user {}", user.getId());
  }

  private void sendQuietly(Runnable send, boolean loginRecovery) {
    try {
      send.run();
    } catch (RuntimeException exception) {
      if (!loginRecovery) {
        passwordResetEmailFailures.increment();
      }
      LOG.warn("Account recovery email was not delivered");
    }
  }

  private String resetUrl(String rawToken) {
    URI origin = URI.create(properties.frontendBaseUrl());
    String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
    return origin.getScheme()
        + "://"
        + origin.getRawAuthority()
        + "/reset-password?token="
        + encoded;
  }

  private String newRawToken() {
    byte[] bytes = new byte[TOKEN_BYTES];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  static String sha256(String value) {
    try {
      byte[] hash =
          MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 not available", exception);
    }
  }

  private static Counter counter(MeterRegistry registry, String name) {
    return Counter.builder(name).register(registry);
  }
}
