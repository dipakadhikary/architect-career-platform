package com.acos.integration.health;

import io.swagger.v3.oas.annotations.media.Schema;

/** Availability status of the AI Platform from the Business Platform perspective. */
@Schema(name = "AiPlatformHealthStatus", description = "AI Platform availability status")
public enum AiPlatformHealthStatus {
  /** The AI Platform is reachable and operating normally. */
  AVAILABLE,

  /** The AI Platform cannot be reached or is out of service. */
  UNAVAILABLE,

  /** The AI Platform is reachable but operating with reduced capability. */
  DEGRADED
}
