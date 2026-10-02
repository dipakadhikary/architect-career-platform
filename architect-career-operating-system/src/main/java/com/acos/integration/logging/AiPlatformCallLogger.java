package com.acos.integration.logging;

import com.acos.common.logging.CorrelationIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Structured logging for AI Platform capability invocations. Never logs request payloads, tokens,
 * or API keys.
 */
@Component
public class AiPlatformCallLogger {

  private static final Logger LOG = LoggerFactory.getLogger(AiPlatformCallLogger.class);

  /**
   * Logs a completed AI Platform call.
   *
   * @param callLog structured call fields
   */
  public void logCall(AiPlatformCallLog callLog) {
    if (LOG.isInfoEnabled()) {
      String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
      LOG.info(
          "ai_platform_call correlationId={} feature={} capability={} endpoint={} latencyMs={}"
              + " status={}",
          correlationId == null ? "-" : correlationId,
          callLog.feature(),
          callLog.capability(),
          callLog.endpoint(),
          callLog.latencyMs(),
          callLog.status());
    }
  }
}
