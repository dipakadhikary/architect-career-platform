package com.acos.auth.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for account recovery. */
@ExtendWith(MockitoExtension.class)
class AccountRecoveryServiceTest {

  private static final String EMAIL = "ada@acos.local";
  private static final String PASSWORD = "Str0ng!Pass12";
  private static final Instant NOW = Instant.parse("2026-10-07T12:00:00Z");

  @Mock private UserRepository userRepository;
  @Mock private PasswordResetTokenRepository tokenRepository;
  @Mock private TokenService tokenService;
  @Mock private AccountMailSender mailSender;

  private PasswordEncoder passwordEncoder;
  private AccountRecoveryService service;
  private User user;

  @BeforeEach
  void setUp() {
    passwordEncoder = new BCryptPasswordEncoder();
    Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    AccountRecoveryProperties properties =
        new AccountRecoveryProperties(
            Duration.ofMinutes(30), "http://localhost:5173", 5, Duration.ofSeconds(60));
    service =
        new AccountRecoveryService(
            userRepository,
            tokenRepository,
            Mappers.getMapper(UserMapper.class),
            passwordEncoder,
            new PasswordValidator(),
            tokenService,
            mailSender,
            properties,
            new RecoveryRateLimiter(clock, properties),
            clock,
            new SimpleMeterRegistry());
    user = new User(EMAIL, passwordEncoder.encode("Old!Password1"), "Ada", "Lovelace");
    ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
  }

  @Test
  void shouldReturnTheSameMessageWhenTheEmailIsUnknown() {
    when(userRepository.findByEmail("missing@acos.local")).thenReturn(Optional.empty());

    RecoveryAcknowledgement known = acknowledgementFor(EMAIL);
    RecoveryAcknowledgement unknown =
        service.requestPasswordReset("missing@acos.local");

    assertThat(known.message()).isEqualTo(unknown.message());
    verify(mailSender, never()).sendPasswordReset(eq("missing@acos.local"), any());
  }

  @Test
  void shouldEmailTheLoginIdentifierWithoutReturningIt() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    RecoveryAcknowledgement response = service.requestLoginIdentifier(EMAIL);

    assertThat(response.message()).doesNotContain(EMAIL);
    verify(mailSender).sendLoginIdentifier(EMAIL, EMAIL);
  }

  @Test
  void shouldStoreOnlyTheResetTokenHashAndRevokeRefreshTokensAfterReset() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(tokenRepository.findUnusedByUserId(user.getId())).thenReturn(List.of());
    ArgumentCaptor<PasswordResetToken> saved = ArgumentCaptor.forClass(PasswordResetToken.class);
    when(tokenRepository.save(saved.capture())).thenAnswer(invocation -> invocation.getArgument(0));
    ArgumentCaptor<String> resetUrl = ArgumentCaptor.forClass(String.class);

    service.requestPasswordReset(EMAIL);
    verify(mailSender).sendPasswordReset(eq(EMAIL), resetUrl.capture());

    String rawToken = resetUrl.getValue().substring(resetUrl.getValue().indexOf("token=") + 6);
    assertThat(saved.getValue().getTokenHash()).isNotEqualTo(rawToken);
    assertThat(saved.getValue().getTokenHash())
        .isEqualTo(AccountRecoveryService.sha256(rawToken));
    assertThat(resetUrl.getValue()).startsWith("http://localhost:5173/reset-password?token=");

    when(tokenRepository.findByTokenHashForUpdate(saved.getValue().getTokenHash()))
        .thenReturn(Optional.of(saved.getValue()));

    service.resetPassword(new ResetPasswordRequest(rawToken, PASSWORD, PASSWORD));

    assertThat(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).isTrue();
    assertThat(passwordEncoder.matches("Old!Password1", user.getPasswordHash())).isFalse();
    verify(tokenService).revokeAllRefreshTokens(user.getId());
    assertThat(saved.getValue().getUsedAt()).isEqualTo(NOW);

    assertThatThrownBy(
            () -> service.resetPassword(new ResetPasswordRequest(rawToken, PASSWORD, PASSWORD)))
        .isInstanceOf(InvalidPasswordResetTokenException.class);
  }

  @Test
  void shouldRejectAnExpiredToken() {
    PasswordResetToken token =
        new PasswordResetToken(
            user, AccountRecoveryService.sha256("raw-token"), NOW.minusSeconds(1));
    when(tokenRepository.findByTokenHashForUpdate(token.getTokenHash()))
        .thenReturn(Optional.of(token));

    assertThatThrownBy(
            () -> service.resetPassword(new ResetPasswordRequest("raw-token", PASSWORD, PASSWORD)))
        .isInstanceOf(InvalidPasswordResetTokenException.class);
    verify(tokenService, never()).revokeAllRefreshTokens(any());
  }

  @Test
  void shouldRejectAPasswordThatDoesNotMatch() {
    assertThatThrownBy(
            () ->
                service.resetPassword(
                    new ResetPasswordRequest("raw-token", PASSWORD, "Other!Pass12")))
        .isInstanceOf(WeakPasswordException.class);
    verify(tokenRepository, never()).findByTokenHashForUpdate(any());
  }

  @Test
  void shouldAllowOnlyOneConcurrentConsume() throws Exception {
    PasswordResetToken token =
        new PasswordResetToken(user, "hash", NOW.plus(Duration.ofMinutes(5)));
    CountDownLatch start = new CountDownLatch(1);
    AtomicInteger successes = new AtomicInteger();
    Thread first = consumer(token, start, successes);
    Thread second = consumer(token, start, successes);
    first.start();
    second.start();
    start.countDown();
    first.join();
    second.join();

    assertThat(successes.get()).isEqualTo(1);
  }

  private RecoveryAcknowledgement acknowledgementFor(String email) {
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(tokenRepository.findUnusedByUserId(user.getId())).thenReturn(List.of());
    when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    return service.requestPasswordReset(email);
  }

  private static Thread consumer(
      PasswordResetToken token, CountDownLatch start, AtomicInteger successes) {
    return new Thread(
        () -> {
          try {
            start.await();
          } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
          }
          if (token.consume(NOW)) {
            successes.incrementAndGet();
          }
        });
  }
}
