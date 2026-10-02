package com.acos.career.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.InterviewRequest;
import com.acos.career.dto.InterviewResponse;
import com.acos.career.service.InterviewService;
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

/** REST API for career interview scheduling. */
@RestController
@RequestMapping("/api/v1/career/applications/{applicationId}/interviews")
@Tag(name = "Career Interviews", description = "Authenticated career interview APIs")
@SecurityRequirement(name = "bearer-jwt")
public class InterviewController {

  private final InterviewService interviewService;

  /**
   * Creates the interview controller.
   *
   * @param interviewService interview service
   */
  public InterviewController(InterviewService interviewService) {
    this.interviewService = interviewService;
  }

  /**
   * Creates an interview under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param request create payload
   * @return created interview
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Create interview",
      description =
          "Schedules an interview under a job application owned by the authenticated user. The"
              + " interview date must not be in the past when scheduling.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Interview created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InterviewApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Interview request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for interview APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Owning job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Archived applications cannot manage interviews",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<InterviewResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Valid @RequestBody InterviewRequest request) {
    InterviewResponse response = interviewService.create(principal.getId(), applicationId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates an interview under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param interviewId interview id
   * @param request update payload
   * @return updated interview
   */
  @PutMapping(
      path = "/{interviewId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Update interview",
      description = "Updates an interview under a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Interview updated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InterviewApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Interview request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for interview APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Interview not found under the authenticated user application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Archived applications cannot manage interviews",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<InterviewResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Parameter(description = "Interview identifier", required = true) @PathVariable
          UUID interviewId,
      @Valid @RequestBody InterviewRequest request) {
    InterviewResponse response =
        interviewService.update(principal.getId(), applicationId, interviewId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes an interview under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param interviewId interview id
   * @return success envelope
   */
  @DeleteMapping(path = "/{interviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Delete interview",
      description = "Deletes an interview under a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Interview deleted",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema =
                    @Schema(implementation = CompanyController.CareerDeleteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for interview APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Interview not found under the authenticated user application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Parameter(description = "Interview identifier", required = true) @PathVariable
          UUID interviewId) {
    interviewService.delete(principal.getId(), applicationId, interviewId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns an interview under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param interviewId interview id
   * @return interview details
   */
  @GetMapping(path = "/{interviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get interview",
      description = "Returns an interview under a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Interview found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InterviewApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for interview APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Interview not found under the authenticated user application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<InterviewResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Parameter(description = "Interview identifier", required = true) @PathVariable
          UUID interviewId) {
    InterviewResponse response =
        interviewService.get(principal.getId(), applicationId, interviewId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists interviews for a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @return interviews
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List interviews",
      description =
          "Lists interviews for a job application owned by the authenticated user, ordered by"
              + " interview date.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Interviews listed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InterviewListApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for interview APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Owning job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<List<InterviewResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId) {
    List<InterviewResponse> response = interviewService.list(principal.getId(), applicationId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful interview envelope.
   *
   * @param success whether the request succeeded
   * @param data interview payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerInterviewApiResponse", description = "Successful career interview envelope")
  public record InterviewApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Interview payload") InterviewResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful interview list envelope.
   *
   * @param success whether the request succeeded
   * @param data interviews payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerInterviewListApiResponse",
      description = "Successful career interview list envelope")
  public record InterviewListApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Interviews payload") List<InterviewResponse> data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
