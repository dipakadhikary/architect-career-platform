package com.acos.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RetryPolicyTest {

  @Test
  void defaultsUseThreeAttempts() {
    RetryPolicy policy = RetryPolicy.defaults();
    assertEquals(3, policy.retries());
    assertEquals(200L, policy.delayMs());
  }

  @Test
  void rejectsInvalidRetries() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryPolicy(0, 100L, 2.0d, java.util.Set.of(500), (e, a) -> false));
  }
}
