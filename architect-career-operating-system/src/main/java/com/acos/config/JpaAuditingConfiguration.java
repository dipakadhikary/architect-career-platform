package com.acos.config;

import java.time.Instant;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Enables JPA auditing for {@code created_at} and {@code updated_at} columns. */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "acosDateTimeProvider")
public class JpaAuditingConfiguration {

  /**
   * Supplies UTC instants for Spring Data auditing annotations.
   *
   * @return date-time provider
   */
  @Bean(name = "acosDateTimeProvider")
  public DateTimeProvider acosDateTimeProvider() {
    return () -> Optional.of(Instant.now());
  }
}
