package com.acos.integration.config;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.logging.CorrelationIdFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Propagates outbound AI Platform headers from the current security and request context. Does not
 * log or expose token contents.
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
    template.header("Accept", "application/json");
    applyApiKey(template);
    applyCorrelationId(template);
    template.header(HEADER_REQUEST_ID, resolveRequestId());
    applyOptionalHeader(template, HEADER_TRACE_ID, resolveTraceId());
    applyUserId(template);
    applyOptionalHeader(template, HttpHeaders.AUTHORIZATION, resolveAuthorizationHeader());
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

  private static String resolveRequestId() {
    String inbound = headerFromCurrentRequest(INBOUND_REQUEST_ID_HEADER);
    if (inbound != null) {
      return inbound;
    }
    return UUID.randomUUID().toString();
  }

  private static String resolveTraceId() {
    return headerFromCurrentRequest(INBOUND_TRACE_ID_HEADER);
  }

  private static String resolveAuthorizationHeader() {
    return headerFromCurrentRequest(HttpHeaders.AUTHORIZATION);
  }

  private static String headerFromCurrentRequest(String headerName) {
    HttpServletRequest request = currentRequest();
    if (request == null) {
      return null;
    }
    String inbound = request.getHeader(headerName);
    if (inbound == null || inbound.isBlank()) {
      return null;
    }
    return inbound.trim();
  }

  private static HttpServletRequest currentRequest() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes instanceof ServletRequestAttributes servletAttributes) {
      return servletAttributes.getRequest();
    }
    return null;
  }
}
