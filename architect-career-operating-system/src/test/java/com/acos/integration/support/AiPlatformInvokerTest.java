package com.acos.integration.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.integration.config.AiPlatformProperties;
import com.acos.integration.logging.AiPlatformCallLogger;
import com.acos.integration.metrics.AiPlatformMetrics;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Unit tests for {@link AiPlatformInvoker} feature toggle and resilience wiring. */
@ExtendWith(MockitoExtension.class)
class AiPlatformInvokerTest {

  @Mock private AiPlatformMetrics metrics;
  @Mock private AiPlatformCallLogger callLogger;

  private AiPlatformInvoker invoker;

  @BeforeEach
  void setUp() {
    AiPlatformProperties properties =
        new AiPlatformProperties(
            false,
            "http://localhost:8090",
            "key",
            Duration.ofSeconds(3),
            Duration.ofSeconds(30),
            "BASIC",
            new AiPlatformProperties.CompressionProperties(true, true, 2048),
            new AiPlatformProperties.ResilienceProperties("ai-platform"),
            new AiPlatformProperties.RetryProperties(
                true, 3, Duration.ofMillis(200), Duration.ofSeconds(2)));
    invoker =
        new AiPlatformInvoker(
            properties,
            metrics,
            callLogger,
            CircuitBreakerRegistry.ofDefaults(),
            RetryRegistry.ofDefaults(),
            BulkheadRegistry.ofDefaults(),
            TimeLimiterRegistry.ofDefaults());
  }

  @Test
  void shouldReturnMockWhenDisabledWithoutCallingLiveSupplier() {
    Runnable live = mock(Runnable.class);

    String result =
        invoker.execute(
            "knowledge",
            "index",
            "/api/v1/ai/knowledge/index",
            () -> {
              live.run();
              return "live";
            },
            () -> "mocked");

    assertThat(result).isEqualTo("mocked");
    verify(metrics)
        .recordMocked(
            org.mockito.ArgumentMatchers.eq("knowledge"),
            org.mockito.ArgumentMatchers.eq("index"),
            org.mockito.ArgumentMatchers.any());
  }

  @Test
  void shouldInvokeLivePathWhenEnabled() {
    AiPlatformProperties properties =
        new AiPlatformProperties(
            true,
            "http://localhost:8090",
            "key",
            Duration.ofSeconds(3),
            Duration.ofSeconds(30),
            "BASIC",
            new AiPlatformProperties.CompressionProperties(true, true, 2048),
            new AiPlatformProperties.ResilienceProperties("ai-platform"),
            new AiPlatformProperties.RetryProperties(
                false, 1, Duration.ofMillis(200), Duration.ofSeconds(2)));
    invoker =
        new AiPlatformInvoker(
            properties,
            metrics,
            callLogger,
            CircuitBreakerRegistry.ofDefaults(),
            RetryRegistry.ofDefaults(),
            BulkheadRegistry.ofDefaults(),
            TimeLimiterRegistry.ofDefaults());

    String result =
        invoker.execute(
            "career",
            "generate-resume",
            "/api/v1/ai/career/resume/generate",
            () -> "live-result",
            () -> "mocked");

    assertThat(result).isEqualTo("live-result");
    verify(metrics)
        .recordSuccess(
            org.mockito.ArgumentMatchers.eq("career"),
            org.mockito.ArgumentMatchers.eq("generate-resume"),
            org.mockito.ArgumentMatchers.any());
  }

  @Test
  void shouldKeepTheCallerRequestVisibleOnTheAsyncCall() {
    AiPlatformProperties properties =
        new AiPlatformProperties(
            true,
            "http://localhost:8090",
            "key",
            Duration.ofSeconds(3),
            Duration.ofSeconds(30),
            "BASIC",
            new AiPlatformProperties.CompressionProperties(true, true, 2048),
            new AiPlatformProperties.ResilienceProperties("ai-platform"),
            new AiPlatformProperties.RetryProperties(
                false, 1, Duration.ofMillis(200), Duration.ofSeconds(2)));
    invoker =
        new AiPlatformInvoker(
            properties,
            metrics,
            callLogger,
            CircuitBreakerRegistry.ofDefaults(),
            RetryRegistry.ofDefaults(),
            BulkheadRegistry.ofDefaults(),
            TimeLimiterRegistry.ofDefaults());
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    try {
      String seen =
          invoker.execute(
              "assistant",
              "conversations",
              "/api/v1/ai/conversations",
              () -> {
                if (!(RequestContextHolder.getRequestAttributes()
                    instanceof ServletRequestAttributes servletAttributes)) {
                  return "missing";
                }
                return servletAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
              },
              () -> "mocked");

      assertThat(seen).isEqualTo("Bearer token");
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }
}
