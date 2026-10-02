package com.acos.auth.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link JwtTokenProvider}. */
class JwtTokenProviderTest {

  private static final String SECRET = "unit-test-secret-key-with-32b-minimum!";
  private static final String ISSUER = "acos-test";
  private static final String EMAIL = "ada@acos.local";
  private static final String ROLE_USER = "USER";
  private static final Instant FIXED_INSTANT = Instant.parse("2026-08-04T06:00:00Z");

  private JwtProperties properties;
  private Clock clock;
  private JwtTokenProvider provider;

  @BeforeEach
  void setUp() {
    properties = new JwtProperties(SECRET, ISSUER, Duration.ofMinutes(15), Duration.ofDays(7));
    clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    provider = new JwtTokenProvider(properties, clock);
  }

  @Test
  void shouldCreateAndValidateAccessToken() {
    UUID userId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    String token = provider.createAccessToken(userId, EMAIL, List.of(ROLE_USER, "ADMIN"));

    assertThat(provider.isValid(token)).isTrue();
    assertThat(provider.isExpired(token)).isFalse();
    assertThat(provider.getUserId(token)).isEqualTo(userId);
    assertThat(provider.getEmail(token)).isEqualTo(EMAIL);
    assertThat(provider.getRoles(token)).containsExactly(ROLE_USER, "ADMIN");
    assertThat(provider.getExpiration(token)).isEqualTo(FIXED_INSTANT.plus(Duration.ofMinutes(15)));
    assertThat(provider.getAccessTokenExpiresInSeconds()).isEqualTo(900L);
  }

  @Test
  void shouldRejectTamperedToken() {
    UUID userId = UUID.randomUUID();
    String token = provider.createAccessToken(userId, EMAIL, List.of(ROLE_USER));
    String tampered = token.substring(0, token.length() - 4) + "dead";

    assertThat(provider.isValid(tampered)).isFalse();
    assertThatThrownBy(() -> provider.parseClaims(tampered)).isInstanceOf(JwtException.class);
  }

  @Test
  void shouldRejectTokenSignedWithDifferentSecret() {
    UUID userId = UUID.randomUUID();
    String token = provider.createAccessToken(userId, EMAIL, List.of(ROLE_USER));

    JwtTokenProvider otherProvider =
        new JwtTokenProvider(
            new JwtProperties(
                "different-secret-key-with-32-bytes!!",
                ISSUER,
                Duration.ofMinutes(15),
                Duration.ofDays(7)),
            clock);

    assertThat(otherProvider.isValid(token)).isFalse();
  }

  @Test
  void shouldDetectExpiredToken() {
    UUID userId = UUID.randomUUID();
    String token = provider.createAccessToken(userId, EMAIL, List.of(ROLE_USER));

    Clock later = Clock.fixed(FIXED_INSTANT.plus(Duration.ofMinutes(16)), ZoneOffset.UTC);
    JwtTokenProvider expiredView = new JwtTokenProvider(properties, later);

    assertThat(expiredView.isValid(token)).isFalse();
    assertThat(expiredView.isExpired(token)).isTrue();
    assertThatThrownBy(() -> expiredView.parseClaims(token))
        .isInstanceOf(ExpiredJwtException.class);
  }

  @Test
  void shouldRejectBlankToken() {
    assertThat(provider.isValid(" ")).isFalse();
    assertThatThrownBy(() -> provider.parseClaims(" "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("blank");
  }

  @Test
  void shouldRejectNullInputsWhenCreatingToken() {
    assertThatThrownBy(() -> provider.createAccessToken(null, EMAIL, List.of()))
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> provider.createAccessToken(UUID.randomUUID(), null, List.of()))
        .isInstanceOf(NullPointerException.class);
  }
}
