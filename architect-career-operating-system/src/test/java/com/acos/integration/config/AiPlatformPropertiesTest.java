package com.acos.integration.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link AiPlatformProperties}. */
class AiPlatformPropertiesTest {

  @Test
  void shouldBindValidProperties() {
    AiPlatformProperties properties =
        new AiPlatformProperties(
            false,
            "http://localhost:8090",
            "secret",
            Duration.ofSeconds(3),
            Duration.ofSeconds(30),
            "BASIC",
            new AiPlatformProperties.CompressionProperties(true, true, 2048),
            new AiPlatformProperties.ResilienceProperties("ai-platform"),
            new AiPlatformProperties.RetryProperties(
                true, 3, Duration.ofMillis(200), Duration.ofSeconds(2)));

    assertThat(properties.enabled()).isFalse();
    assertThat(properties.baseUrl()).isEqualTo("http://localhost:8090");
    assertThat(properties.resilience().instance()).isEqualTo("ai-platform");
    assertThat(properties.compression().minRequestSize()).isEqualTo(2048);
  }

  @Test
  void shouldRejectNonPositiveTimeouts() {
    assertThatThrownBy(
            () ->
                new AiPlatformProperties(
                    true,
                    "http://localhost:8090",
                    "secret",
                    Duration.ZERO,
                    Duration.ofSeconds(30),
                    "BASIC",
                    new AiPlatformProperties.CompressionProperties(true, true, 2048),
                    new AiPlatformProperties.ResilienceProperties("ai-platform"),
                    new AiPlatformProperties.RetryProperties(
                        true, 3, Duration.ofMillis(200), Duration.ofSeconds(2))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("connection-timeout");
  }
}
