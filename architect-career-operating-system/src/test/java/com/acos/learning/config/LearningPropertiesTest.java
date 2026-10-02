package com.acos.learning.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link LearningProperties}. */
class LearningPropertiesTest {

  @Test
  void shouldRejectNonPositiveLimits() {
    assertThatThrownBy(() -> new LearningProperties(0, 10, 10))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new LearningProperties(10, 0, 10))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new LearningProperties(10, 10, 0))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
