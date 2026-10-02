package com.acos.auth.config;

import com.acos.auth.token.JwtProperties;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Enables JWT configuration properties binding and shared time source. */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {

  /**
   * Provides a UTC clock for token issuance and validation.
   *
   * @return system UTC clock
   */
  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
