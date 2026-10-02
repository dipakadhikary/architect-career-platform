package com.acos.career.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.RecruiterRequest;
import com.acos.career.dto.RecruiterResponse;
import com.acos.career.service.RecruiterService;
import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for career recruiter management. */
@RestController
@RequestMapping("/api/v1/career/recruiters")
@Tag(name = "Career Recruiters", description = "Authenticated career recruiter APIs")
@SecurityRequirement(name = "bearer-jwt")
public class RecruiterController {

  private final RecruiterService recruiterService;

  /**
   * Creates the recruiter controller.
   *
   * @param recruiterService recruiter service
   */
  public RecruiterController(RecruiterService recruiterService) {
    this.recruiterService = recruiterService;
  }

  /**
   * Creates a recruiter for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created recruiter
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Create recruiter",
      description =
          "Creates a recruiter contact owned by the authenticated user. Email addresses must be"
              + " unique per owner when provided.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Recruiter created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RecruiterApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Recruiter request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for recruiter APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Referenced company not found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Duplicate recruiter email",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<RecruiterResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody RecruiterRequest request) {
    RecruiterResponse response = recruiterService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a recruiter owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param recruiterId recruiter id
   * @param request update payload
   * @return updated recruiter
   */
  @PutMapping(
      path = "/{recruiterId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Update recruiter",
      description = "Updates a recruiter owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Recruiter updated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RecruiterApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Recruiter request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for recruiter APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Recruiter not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Duplicate recruiter email",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<RecruiterResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Recruiter identifier", required = true) @PathVariable
          UUID recruiterId,
      @Valid @RequestBody RecruiterRequest request) {
    RecruiterResponse response = recruiterService.update(principal.getId(), recruiterId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a recruiter owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param recruiterId recruiter id
   * @return success envelope
   */
  @DeleteMapping(path = "/{recruiterId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Delete recruiter",
      description = "Deletes a recruiter owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Recruiter deleted",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema =
                    @Schema(implementation = CompanyController.CareerDeleteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for recruiter APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Recruiter not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Recruiter identifier", required = true) @PathVariable
          UUID recruiterId) {
    recruiterService.delete(principal.getId(), recruiterId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a recruiter owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param recruiterId recruiter id
   * @return recruiter details
   */
  @GetMapping(path = "/{recruiterId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get recruiter",
      description = "Returns a recruiter owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Recruiter found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RecruiterApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for recruiter APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Recruiter not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<RecruiterResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Recruiter identifier", required = true) @PathVariable
          UUID recruiterId) {
    RecruiterResponse response = recruiterService.get(principal.getId(), recruiterId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists recruiters for the authenticated user.
   *
   * @param principal authenticated principal
   * @return recruiters
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List recruiters",
      description = "Lists all recruiters owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Recruiters listed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RecruiterListApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for recruiter APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<List<RecruiterResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal) {
    List<RecruiterResponse> response = recruiterService.list(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful recruiter envelope.
   *
   * @param success whether the request succeeded
   * @param data recruiter payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerRecruiterApiResponse", description = "Successful career recruiter envelope")
  public record RecruiterApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Recruiter payload") RecruiterResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful recruiter list envelope.
   *
   * @param success whether the request succeeded
   * @param data recruiters payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerRecruiterListApiResponse",
      description = "Successful career recruiter list envelope")
  public record RecruiterListApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Recruiters payload") List<RecruiterResponse> data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
