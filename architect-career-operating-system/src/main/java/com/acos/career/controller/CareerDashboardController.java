package com.acos.career.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.CareerDashboardResponse;
import com.acos.career.service.CareerDashboardService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for career dashboard aggregation. */
@RestController
@RequestMapping("/api/v1/career/dashboard")
@Tag(name = "Career Dashboard", description = "Authenticated career dashboard APIs")
@SecurityRequirement(name = "bearer-jwt")
public class CareerDashboardController {

  private final CareerDashboardService careerDashboardService;

  /**
   * Creates the career dashboard controller.
   *
   * @param careerDashboardService dashboard service
   */
  public CareerDashboardController(CareerDashboardService careerDashboardService) {
    this.careerDashboardService = careerDashboardService;
  }

  /**
   * Returns the career tracker dashboard summary for the authenticated user.
   *
   * @param principal authenticated principal
   * @return dashboard summary
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get career dashboard summary",
      description =
          "Returns dynamically calculated career dashboard metrics for the authenticated user,"
              + " including total applications, applications by status, scheduled interviews,"
              + " offers received, rejected applications, acceptance ratio, and average interview"
              + " rating.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Dashboard summary returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerDashboardApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for career dashboard",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<CareerDashboardResponse>> getSummary(
      @AuthenticationPrincipal AcosUserDetails principal) {
    CareerDashboardResponse response = careerDashboardService.getSummary(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful career dashboard envelope.
   *
   * @param success whether the request succeeded
   * @param data dashboard payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerDashboardApiResponse", description = "Successful career dashboard envelope")
  public record CareerDashboardApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Dashboard payload") CareerDashboardResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
