package com.acos.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Propagates a correlation identifier via request/response headers and SLF4J MDC. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

  /** HTTP header used to accept and return the correlation identifier. */
  public static final String HEADER_NAME = "X-Correlation-Id";

  /** MDC key used for structured logging. */
  public static final String MDC_KEY = "correlationId";

  /**
   * Resolves or generates a correlation id, stores it in MDC, and continues the chain.
   *
   * @param request HTTP request
   * @param response HTTP response
   * @param filterChain remaining filter chain
   * @throws ServletException if the chain fails
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = resolveCorrelationId(request);
    MDC.put(MDC_KEY, correlationId);
    response.setHeader(HEADER_NAME, correlationId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }

  private static String resolveCorrelationId(HttpServletRequest request) {
    String incoming = request.getHeader(HEADER_NAME);
    if (incoming == null || incoming.isBlank()) {
      return UUID.randomUUID().toString();
    }
    return incoming.trim();
  }
}
