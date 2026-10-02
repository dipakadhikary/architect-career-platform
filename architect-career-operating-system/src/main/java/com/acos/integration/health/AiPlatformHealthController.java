package com.acos.integration.health;

import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for AI Platform integration health. */
@RestController
@RequestMapping("/api/v1/integration/ai")
@Tag(name = "AI Integration", description = "AI Platform integration foundation APIs")
public class AiPlatformHealthController {

  private final AiPlatformHealthService healthService;

  /**
   * Creates the health controller.
   *
   * @param healthService AI Platform health service
   */
  public AiPlatformHealthController(AiPlatformHealthService healthService) {
    this.healthService = healthService;
  }

  /**
   * Returns the AI Platform integration health status.
   *
   * @return health status wrapped in {@link ApiResponse}
   */
  @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirement(name = "bearer-jwt")
  @Operation(
      summary = "Get AI Platform health",
      description =
          "Returns the availability of the AI Platform integration. Status values are AVAILABLE,"
              + " UNAVAILABLE, or DEGRADED. When integration is disabled the status is UNAVAILABLE"
              + " and capability gateways return mocked responses.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "AI Platform health status returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HealthApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<AiPlatformHealthResponse>> getHealth() {
    return ResponseEntity.ok(ApiResponse.success(healthService.checkHealth()));
  }

  /**
   * OpenAPI schema for a successful health envelope.
   *
   * @param success whether the request succeeded
   * @param data health payload
   * @param error always {@code null} on success
   * @param timestamp response timestamp
   */
  @Schema(name = "AiPlatformHealthApiResponse")
  public record HealthApiResponse(
      boolean success, AiPlatformHealthResponse data, ApiError error, Instant timestamp) {}

  /**
   * OpenAPI schema for an error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on error
   * @param error error payload
   * @param timestamp response timestamp
   */
  @Schema(name = "AiPlatformHealthErrorApiResponse")
  public record ErrorApiResponse(boolean success, Object data, ApiError error, Instant timestamp) {}
}
