package com.acos.integration.config;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.logging.CorrelationIdFilter;
import com.acos.integration.support.AiPlatformOutboundContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Propagates outbound AI Platform headers from the current security and request context. Does not
 * log or expose token contents.
 *
 * <p>Prefers {@link AiPlatformOutboundContext} snapshots so async / retry Feign calls never read a
 * recycled servlet request.
 */
public class AiPlatformRequestInterceptor implements RequestInterceptor {

  /** Outbound correlation header aligned with the inbound Business Platform filter. */
  public static final String HEADER_CORRELATION_ID = CorrelationIdFilter.HEADER_NAME;

  /** Per-call request identifier for the AI Platform. */
  public static final String HEADER_REQUEST_ID = "X-Request-Id";

  /** Optional distributed trace identifier when present on the inbound request. */
  public static final String HEADER_TRACE_ID = "X-Trace-Id";

  /** Authenticated user identifier forwarded to the AI Platform. */
  public static final String HEADER_USER_ID = "X-User-Id";

  /** Inbound request-id header accepted from callers when present. */
  public static final String INBOUND_REQUEST_ID_HEADER = "X-Request-Id";

  /** Inbound trace-id header accepted from callers when present. */
  public static final String INBOUND_TRACE_ID_HEADER = "X-Trace-Id";

  private final AiPlatformProperties properties;

  /**
   * Creates the interceptor.
   *
   * @param properties AI Platform properties
   */
  public AiPlatformRequestInterceptor(AiPlatformProperties properties) {
    this.properties = properties;
  }

  @Override
  public void apply(RequestTemplate template) {
    AiPlatformOutboundContext.Snapshot snapshot = resolveSnapshot();
    template.header("Accept", "application/json");
    applyApiKey(template);
    applyCorrelationId(template);
    template.header(HEADER_REQUEST_ID, resolveRequestId(snapshot));
    applyOptionalHeader(template, HEADER_TRACE_ID, snapshot.traceId());
    applyUserId(template);
    applyOptionalHeader(template, HttpHeaders.AUTHORIZATION, snapshot.authorization());
  }

  private void applyApiKey(RequestTemplate template) {
    applyOptionalHeader(template, "X-API-Key", properties.apiKey());
  }

  private static void applyCorrelationId(RequestTemplate template) {
    applyOptionalHeader(template, HEADER_CORRELATION_ID, MDC.get(CorrelationIdFilter.MDC_KEY));
  }

  private static void applyUserId(RequestTemplate template) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.getPrincipal() instanceof AcosUserDetails userDetails) {
      template.header(HEADER_USER_ID, userDetails.getId().toString());
    }
  }

  private static void applyOptionalHeader(RequestTemplate template, String name, String value) {
    if (value != null && !value.isBlank()) {
      template.header(name, value);
    }
  }

  private static AiPlatformOutboundContext.Snapshot resolveSnapshot() {
    AiPlatformOutboundContext.Snapshot bound = AiPlatformOutboundContext.get();
    if (bound != null) {
      return bound;
    }
    return AiPlatformOutboundContext.capture();
  }

  private static String resolveRequestId(AiPlatformOutboundContext.Snapshot snapshot) {
    if (snapshot.requestId() != null && !snapshot.requestId().isBlank()) {
      return snapshot.requestId();
    }
    return UUID.randomUUID().toString();
  }
}
