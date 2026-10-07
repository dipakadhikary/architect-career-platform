package com.acos.auth.recovery;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * In-memory cooldown for public recovery endpoints. The same key is used whether or not an account
 * exists, so the limiter does not reveal that fact.
 */
@Component
public class RecoveryRateLimiter {

  private final Clock clock;
  private final AccountRecoveryProperties properties;
  private final ConcurrentHashMap<String, Deque<Instant>> hits = new ConcurrentHashMap<>();

  /**
   * Creates the limiter.
   *
   * @param clock time source
   * @param properties recovery limits
   */
  public RecoveryRateLimiter(Clock clock, AccountRecoveryProperties properties) {
    this.clock = clock;
    this.properties = properties;
  }

  /**
   * Records an attempt when the key is inside the configured window.
   *
   * @param key normalized email
   * @return {@code true} when the attempt is allowed
   */
  public boolean allow(String key) {
    Instant now = Instant.now(clock);
    Deque<Instant> window = hits.computeIfAbsent(key, ignored -> new ArrayDeque<>());
    synchronized (window) {
      Instant hourAgo = now.minus(Duration.ofHours(1));
      while (!window.isEmpty() && window.peekFirst().isBefore(hourAgo)) {
        window.removeFirst();
      }
      if (!window.isEmpty() && window.peekLast().plus(properties.minInterval()).isAfter(now)) {
        return false;
      }
      if (window.size() >= properties.maxRequestsPerHour()) {
        return false;
      }
      window.addLast(now);
      return true;
    }
  }
}
