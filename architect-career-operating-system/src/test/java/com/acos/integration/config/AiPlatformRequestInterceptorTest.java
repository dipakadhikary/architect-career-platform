package com.acos.integration.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.logging.CorrelationIdFilter;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Unit tests for {@link AiPlatformRequestInterceptor}. */
class AiPlatformRequestInterceptorTest {

  @AfterEach
  void tearDown() {
    MDC.clear();
    SecurityContextHolder.clearContext();
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void shouldPropagateCorrelationRequestUserAuthApiKeyAndTrace() {
    UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    MDC.put(CorrelationIdFilter.MDC_KEY, "corr-1");
    AcosUserDetails principal =
        new AcosUserDetails(
            userId,
            "user@example.com",
            "hash",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(AiPlatformRequestInterceptor.INBOUND_REQUEST_ID_HEADER))
        .thenReturn("req-1");
    when(request.getHeader(AiPlatformRequestInterceptor.INBOUND_TRACE_ID_HEADER))
        .thenReturn("trace-1");
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    AiPlatformProperties properties =
        new AiPlatformProperties(
            true,
            "http://localhost:8090",
            "api-secret",
            java.time.Duration.ofSeconds(3),
            java.time.Duration.ofSeconds(30),
            "BASIC",
            new AiPlatformProperties.CompressionProperties(true, true, 2048),
            new AiPlatformProperties.ResilienceProperties("ai-platform"),
            new AiPlatformProperties.RetryProperties(
                true, 3, java.time.Duration.ofMillis(200), java.time.Duration.ofSeconds(2)));
    AiPlatformRequestInterceptor interceptor = new AiPlatformRequestInterceptor(properties);
    RequestTemplate template = new RequestTemplate();

    interceptor.apply(template);

    Map<String, Collection<String>> headers = template.headers();
    assertThat(headers.get(AiPlatformRequestInterceptor.HEADER_CORRELATION_ID))
        .containsExactly("corr-1");
    assertThat(headers.get(AiPlatformRequestInterceptor.HEADER_REQUEST_ID))
        .containsExactly("req-1");
    assertThat(headers.get(AiPlatformRequestInterceptor.HEADER_TRACE_ID))
        .containsExactly("trace-1");
    assertThat(headers.get(AiPlatformRequestInterceptor.HEADER_USER_ID))
        .containsExactly(userId.toString());
    assertThat(headers.get(HttpHeaders.AUTHORIZATION)).containsExactly("Bearer token");
    assertThat(headers.get("X-API-Key")).containsExactly("api-secret");
  }
}
