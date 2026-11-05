package com.acos.integration.support;

import com.acos.integration.config.AiPlatformRequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Immutable outbound headers for AI Platform Feign calls.
 *
 * <p>Async listeners and Resilience4j retries must not keep a live {@link HttpServletRequest}:
 * Tomcat recycles it after the inbound request finishes. Capture values once instead.
 */
public final class AiPlatformOutboundContext {

  private static final ThreadLocal<Snapshot> HOLDER = new ThreadLocal<>();

  private AiPlatformOutboundContext() {}

  /**
   * Outbound header values copied from the inbound request.
   *
   * @param authorization optional bearer token
   * @param requestId optional inbound request id
   * @param traceId optional inbound trace id
   */
  public record Snapshot(String authorization, String requestId, String traceId) {

    /** Empty snapshot when no inbound request is available. */
    public static Snapshot empty() {
      return new Snapshot(null, null, null);
    }
  }

  /**
   * Captures auth and correlation headers from the current request, if still readable.
   *
   * @return immutable snapshot (never {@code null})
   */
  public static Snapshot capture() {
    HttpServletRequest request = currentRequestOrNull();
    if (request == null) {
      Snapshot existing = HOLDER.get();
      return existing != null ? existing : Snapshot.empty();
    }
    return new Snapshot(
        safeHeader(request, HttpHeaders.AUTHORIZATION),
        safeHeader(request, AiPlatformRequestInterceptor.INBOUND_REQUEST_ID_HEADER),
        safeHeader(request, AiPlatformRequestInterceptor.INBOUND_TRACE_ID_HEADER));
  }

  /**
   * Binds a snapshot to the current thread for Feign interception.
   *
   * @param snapshot outbound headers
   */
  public static void set(Snapshot snapshot) {
    if (snapshot == null) {
      HOLDER.remove();
      return;
    }
    HOLDER.set(snapshot);
  }

  /**
   * Returns the snapshot bound to this thread, if any.
   *
   * @return snapshot or {@code null}
   */
  public static Snapshot get() {
    return HOLDER.get();
  }

  /** Clears the thread-local snapshot. */
  public static void clear() {
    HOLDER.remove();
  }

  private static HttpServletRequest currentRequestOrNull() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
      return null;
    }
    try {
      return servletAttributes.getRequest();
    } catch (IllegalStateException | IllegalArgumentException ex) {
      return null;
    }
  }

  private static String safeHeader(HttpServletRequest request, String name) {
    try {
      String value = request.getHeader(name);
      if (value == null || value.isBlank()) {
        return null;
      }
      return value.trim();
    } catch (IllegalStateException | IllegalArgumentException ex) {
      return null;
    }
  }
}
