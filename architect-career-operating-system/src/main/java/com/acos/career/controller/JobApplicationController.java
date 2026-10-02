package com.acos.career.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.ApplicationStatusHistoryResponse;
import com.acos.career.dto.ApplicationTimelineResponse;
import com.acos.career.dto.JobApplicationPageResponse;
import com.acos.career.dto.JobApplicationRequest;
import com.acos.career.dto.JobApplicationResponse;
import com.acos.career.dto.JobApplicationSearchCriteria;
import com.acos.career.dto.StatusTransitionRequest;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.InterviewRound;
import com.acos.career.service.JobApplicationService;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST API for career job application management, including the status lifecycle. */
@RestController
@RequestMapping("/api/v1/career/applications")
@Tag(name = "Career Applications", description = "Authenticated career job application APIs")
@SecurityRequirement(name = "bearer-jwt")
public class JobApplicationController {

  private final JobApplicationService jobApplicationService;

  /**
   * Creates the job application controller.
   *
   * @param jobApplicationService application service
   */
  public JobApplicationController(JobApplicationService jobApplicationService) {
    this.jobApplicationService = jobApplicationService;
  }

  /**
   * Creates a job application for the authenticated user, always starting in {@code DRAFT}.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created application
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Create job application",
      description =
          "Creates a job application owned by the authenticated user. New applications always"
              + " start in DRAFT. Status changes must use the dedicated status endpoint.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Job application created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Job application request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Referenced company or recruiter not found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody JobApplicationRequest request) {
    JobApplicationResponse response = jobApplicationService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates the mutable fields of a non-archived job application owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param request update payload
   * @return updated application
   */
  @PutMapping(
      path = "/{applicationId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Update job application",
      description =
          "Updates mutable fields of a non-archived job application owned by the authenticated"
              + " user. Status is not changed by this endpoint.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Job application updated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Job application request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Archived job applications cannot be modified",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Valid @RequestBody JobApplicationRequest request) {
    JobApplicationResponse response =
        jobApplicationService.update(principal.getId(), applicationId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Soft-archives a job application owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @return success envelope
   */
  @DeleteMapping(path = "/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Archive job application",
      description =
          "Soft-archives a job application owned by the authenticated user. Archived"
              + " applications are excluded from default listings and search.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Job application archived",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema =
                    @Schema(implementation = CompanyController.CareerDeleteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> archive(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId) {
    jobApplicationService.archive(principal.getId(), applicationId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a job application owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @return application details
   */
  @GetMapping(path = "/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get job application",
      description = "Returns a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Job application found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId) {
    JobApplicationResponse response = jobApplicationService.get(principal.getId(), applicationId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists job applications for the authenticated user, excluding archived applications by default.
   *
   * @param principal authenticated principal
   * @param archived {@code true} to list archived applications instead of active ones
   * @param pageable paging and sorting
   * @return page of applications
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List job applications",
      description =
          "Lists job applications owned by the authenticated user with pagination and sorting."
              + " Archived applications are excluded unless archived=true.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Job applications listed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationPageApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid job application pageable request",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationPageResponse>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Include archived applications instead of active ones")
          @RequestParam(name = "archived", defaultValue = "false")
          boolean archived,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    JobApplicationPageResponse response =
        jobApplicationService.list(principal.getId(), pageable, archived);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists archived job applications for the authenticated user.
   *
   * @param principal authenticated principal
   * @param pageable paging and sorting
   * @return page of archived applications
   */
  @GetMapping(path = "/archived", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List archived job applications",
      description = "Lists soft-archived job applications owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Archived job applications listed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationPageApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationPageResponse>> listArchived(
      @AuthenticationPrincipal AcosUserDetails principal,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    JobApplicationPageResponse response =
        jobApplicationService.list(principal.getId(), pageable, true);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Searches non-archived job applications using optional filters.
   *
   * @param principal authenticated principal
   * @param companyId optional target company id
   * @param recruiterId optional recruiter id
   * @param status optional application status
   * @param interviewRound optional interview round filter
   * @param appliedFrom optional lower bound on the application date
   * @param appliedTo optional upper bound on the application date
   * @param salaryMin optional lower bound on the salary expectation
   * @param salaryMax optional upper bound on the salary expectation
   * @param keyword optional keyword matched against title, job description, and notes
   * @param pageable paging and sorting
   * @return page of matching applications
   */
  @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Search job applications",
      description =
          "Searches non-archived job applications owned by the authenticated user using optional"
              + " filters for company, recruiter, status, interview round, applied date range,"
              + " salary range, and keyword. Supports Spring Pageable and Sort.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Matching job applications returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationPageApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid job application search pageable or filter request",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationPageResponse>> search(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Optional company identifier filter") @RequestParam(required = false)
          UUID companyId,
      @Parameter(description = "Optional recruiter identifier filter")
          @RequestParam(required = false)
          UUID recruiterId,
      @Parameter(description = "Optional application status filter") @RequestParam(required = false)
          ApplicationStatus status,
      @Parameter(description = "Optional interview round filter") @RequestParam(required = false)
          InterviewRound interviewRound,
      @Parameter(description = "Optional applied-on lower bound (inclusive)")
          @RequestParam(required = false)
          LocalDate appliedFrom,
      @Parameter(description = "Optional applied-on upper bound (inclusive)")
          @RequestParam(required = false)
          LocalDate appliedTo,
      @Parameter(description = "Optional salary expectation lower bound")
          @RequestParam(required = false)
          BigDecimal salaryMin,
      @Parameter(description = "Optional salary expectation upper bound")
          @RequestParam(required = false)
          BigDecimal salaryMax,
      @Parameter(description = "Optional keyword matched against title, description, and notes")
          @RequestParam(required = false)
          String keyword,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    JobApplicationSearchCriteria criteria =
        new JobApplicationSearchCriteria(
            companyId,
            recruiterId,
            status,
            interviewRound,
            appliedFrom,
            appliedTo,
            salaryMin,
            salaryMax,
            keyword);
    JobApplicationPageResponse response =
        jobApplicationService.search(principal.getId(), criteria, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Transitions the status of a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param request transition payload
   * @return updated application
   */
  @PatchMapping(
      path = "/{applicationId}/status",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Transition job application status",
      description =
          "Applies an allowed status transition for a non-archived job application. Invalid"
              + " transitions are rejected by the application state machine. A status history"
              + " entry is recorded for every successful change.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Status transitioned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JobApplicationApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Status transition payload validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for status transitions",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Application for status transition was not found for this user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Illegal application status transition or archived application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<JobApplicationResponse>> transitionStatus(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Valid @RequestBody StatusTransitionRequest request) {
    JobApplicationResponse response =
        jobApplicationService.transitionStatus(principal.getId(), applicationId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Returns the status transition history for a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @return status history
   */
  @GetMapping(path = "/{applicationId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get job application status history",
      description =
          "Returns the complete ordered status history for a job application owned by the"
              + " authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Status history returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApplicationHistoryApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<List<ApplicationStatusHistoryResponse>>> getHistory(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId) {
    List<ApplicationStatusHistoryResponse> response =
        jobApplicationService.getHistory(principal.getId(), applicationId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Returns the milestone timeline for a job application, for visualization.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @return timeline milestones
   */
  @GetMapping(path = "/{applicationId}/timeline", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get job application timeline",
      description =
          "Returns the ordered application timeline milestones (Applied → Screening → Technical"
              + " Interview → Manager Interview → HR Interview → Offer → Accepted), including"
              + " alternate terminal outcomes when applicable.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Timeline returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApplicationTimelineApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for application APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Job application not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<ApplicationTimelineResponse>> getTimeline(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId) {
    ApplicationTimelineResponse response =
        jobApplicationService.getTimeline(principal.getId(), applicationId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful job application envelope.
   *
   * @param success whether the request succeeded
   * @param data application payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerJobApplicationApiResponse",
      description = "Successful career job application envelope")
  public record JobApplicationApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Job application payload") JobApplicationResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful paginated job application envelope.
   *
   * @param success whether the request succeeded
   * @param data page payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerJobApplicationPageApiResponse",
      description = "Successful paginated career job applications envelope")
  public record JobApplicationPageApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Paginated job applications payload") JobApplicationPageResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful status history envelope.
   *
   * @param success whether the request succeeded
   * @param data history payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerApplicationHistoryApiResponse",
      description = "Successful career application status history envelope")
  public record ApplicationHistoryApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Status history payload") List<ApplicationStatusHistoryResponse> data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful timeline envelope.
   *
   * @param success whether the request succeeded
   * @param data timeline payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerApplicationTimelineApiResponse",
      description = "Successful career application timeline envelope")
  public record ApplicationTimelineApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Timeline payload") ApplicationTimelineResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
