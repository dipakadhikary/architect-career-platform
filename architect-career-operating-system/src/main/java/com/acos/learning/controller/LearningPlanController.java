package com.acos.learning.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.learning.dto.LearningPlanPageResponse;
import com.acos.learning.dto.LearningPlanRequest;
import com.acos.learning.dto.LearningPlanResponse;
import com.acos.learning.service.LearningPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for learning plan management. */
@RestController
@RequestMapping("/api/v1/learning/plans")
@Tag(name = "Learning Plans", description = "Authenticated learning roadmap plan APIs")
@SecurityRequirement(name = "bearer-jwt")
public class LearningPlanController {

  private final LearningPlanService learningPlanService;

  /**
   * Creates the learning plan controller.
   *
   * @param learningPlanService plan service
   */
  public LearningPlanController(LearningPlanService learningPlanService) {
    this.learningPlanService = learningPlanService;
  }

  /**
   * Creates a learning plan for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created plan
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create learning plan")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Plan created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PlanApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = LearningErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = LearningErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<LearningPlanResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody LearningPlanRequest request) {
    LearningPlanResponse response = learningPlanService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a learning plan owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param request update payload
   * @return updated plan
   */
  @PutMapping(
      path = "/{planId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update learning plan")
  public ResponseEntity<ApiResponse<LearningPlanResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Valid @RequestBody LearningPlanRequest request) {
    LearningPlanResponse response = learningPlanService.update(principal.getId(), planId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a learning plan owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @return success envelope
   */
  @DeleteMapping(path = "/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete learning plan")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId) {
    learningPlanService.delete(principal.getId(), planId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a learning plan with milestones, topics, and progress.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @return plan details
   */
  @GetMapping(path = "/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get learning plan")
  public ResponseEntity<ApiResponse<LearningPlanResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId) {
    LearningPlanResponse response = learningPlanService.get(principal.getId(), planId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists learning plans for the authenticated user.
   *
   * @param principal authenticated principal
   * @param pageable paging and sorting
   * @return page of plan summaries
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List learning plans")
  public ResponseEntity<ApiResponse<LearningPlanPageResponse>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    LearningPlanPageResponse response = learningPlanService.list(principal.getId(), pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful plan envelope.
   *
   * @param success whether the request succeeded
   * @param data plan payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "LearningPlanApiResponse", description = "Successful learning plan envelope")
  public record PlanApiResponse(
      boolean success,
      LearningPlanResponse data,
      ApiError error,
      String correlationId,
      Instant timestamp) {}

  /**
   * OpenAPI schema for a learning error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on failure
   * @param error error payload
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "LearningErrorApiResponse", description = "Learning error response envelope")
  public record LearningErrorApiResponse(
      boolean success, Void data, ApiError error, String correlationId, Instant timestamp) {}
}
