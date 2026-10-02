package com.acos.integration.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;

/**
 * Centralized OpenFeign customizations for all AI Platform clients. Not annotated with
 * {@code @Configuration} so beans remain scoped to Feign clients that reference this class.
 */
public class AiPlatformFeignConfiguration {

  /**
   * Applies connect and read timeouts from configuration properties.
   *
   * @param properties AI Platform properties
   * @return Feign request options
   */
  @Bean
  public Request.Options aiPlatformRequestOptions(AiPlatformProperties properties) {
    return new Request.Options(
        properties.connectionTimeout().toMillis(),
        TimeUnit.MILLISECONDS,
        properties.readTimeout().toMillis(),
        TimeUnit.MILLISECONDS,
        true);
  }

  /**
   * Configures Feign request logging verbosity.
   *
   * @param properties AI Platform properties
   * @return Feign logger level
   */
  @Bean
  public Logger.Level aiPlatformLoggerLevel(AiPlatformProperties properties) {
    return Logger.Level.valueOf(properties.loggerLevel().trim().toUpperCase(Locale.ROOT));
  }

  /**
   * Propagates correlation, request, user, and authorization headers to the AI Platform.
   *
   * @param properties AI Platform properties
   * @return request interceptor
   */
  @Bean
  public RequestInterceptor aiPlatformRequestInterceptor(AiPlatformProperties properties) {
    return new AiPlatformRequestInterceptor(properties);
  }

  /**
   * Disables Feign-native retries so Resilience4j owns retry policy for AI calls.
   *
   * @return Feign retryer that never retries
   */
  @Bean
  public Retryer aiPlatformRetryer() {
    return Retryer.NEVER_RETRY;
  }

  /**
   * Maps AI Platform HTTP error responses to typed integration exceptions.
   *
   * @return Feign error decoder
   */
  @Bean
  public ErrorDecoder aiPlatformErrorDecoder() {
    return new AiPlatformErrorDecoder();
  }
}
