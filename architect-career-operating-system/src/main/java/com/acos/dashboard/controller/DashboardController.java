package com.acos.dashboard.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.dashboard.dto.DashboardResponse;
import com.acos.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for dashboard summaries. */
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Authenticated learning dashboard APIs")
public class DashboardController {

  private final DashboardService dashboardService;

  /**
   * Creates the dashboard controller.
   *
   * @param dashboardService dashboard service
   */
  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  /**
   * Returns the dashboard summary for the authenticated user.
   *
   * @param principal authenticated principal
   * @return dashboard summary wrapped in {@link ApiResponse}
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirement(name = "bearer-jwt")
  @Operation(
      summary = "Get dashboard",
      description =
          "Returns a dashboard summary for the authenticated user. Metric values are placeholders"
              + " until analytics aggregation is implemented. The caller identity is taken from the"
              + " security context; userId must not be supplied as a request parameter.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Dashboard summary returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DashboardApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
      @AuthenticationPrincipal AcosUserDetails principal) {
    DashboardResponse response = dashboardService.getDashboard(principal.getUsername());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful dashboard envelope.
   *
   * @param success whether the request succeeded
   * @param data dashboard payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "DashboardApiResponse", description = "Successful dashboard envelope")
  public record DashboardApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Dashboard payload") DashboardResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for an error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on failure
   * @param error error payload
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "DashboardErrorApiResponse", description = "Dashboard error response envelope")
  public record ErrorApiResponse(
      @Schema(description = "Whether the request succeeded", example = "false") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload") ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
