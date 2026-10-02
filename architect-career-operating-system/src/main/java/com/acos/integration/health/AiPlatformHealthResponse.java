package com.acos.integration.health;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Health check payload for the AI Platform integration.
 *
 * @param status availability status
 * @param message human-readable status detail
 * @param checkedAt time the status was determined
 * @param enabled whether outbound AI Platform calls are enabled
 */
@Schema(name = "AiPlatformHealthResponse", description = "AI Platform integration health status")
public record AiPlatformHealthResponse(
    @Schema(description = "Availability status", requiredMode = Schema.RequiredMode.REQUIRED)
        AiPlatformHealthStatus status,
    @Schema(description = "Human-readable status detail") String message,
    @Schema(description = "Time the status was determined") Instant checkedAt,
    @Schema(description = "Whether outbound AI Platform calls are enabled") boolean enabled) {}
