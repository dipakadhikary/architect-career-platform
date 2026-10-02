package com.acos.auth.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link JwtProperties}. */
class JwtPropertiesTest {

  private static final String SECRET = "unit-test-secret-key-with-32b-minimum!";
  private static final String ISSUER = "acos-test";

  @Test
  void shouldAcceptValidProperties() {
    JwtProperties properties =
        new JwtProperties(SECRET, ISSUER, Duration.ofMinutes(15), Duration.ofDays(7));

    assertThat(properties.issuer()).isEqualTo(ISSUER);
    assertThat(properties.accessTokenTtl()).isEqualTo(Duration.ofMinutes(15));
  }

  @Test
  void shouldRejectShortSecret() {
    assertThatThrownBy(
            () ->
                new JwtProperties("too-short", ISSUER, Duration.ofMinutes(15), Duration.ofDays(7)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("32 bytes");
  }

  @Test
  void shouldRejectNonPositiveTtl() {
    assertThatThrownBy(() -> new JwtProperties(SECRET, ISSUER, Duration.ZERO, Duration.ofDays(7)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("access-token-ttl");
  }
}
