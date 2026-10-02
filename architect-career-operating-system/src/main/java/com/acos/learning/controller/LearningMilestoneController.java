package com.acos.learning.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.learning.dto.LearningMilestoneRequest;
import com.acos.learning.dto.LearningMilestoneResponse;
import com.acos.learning.service.LearningMilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

/** REST API for learning milestone management. */
@RestController
@RequestMapping("/api/v1/learning/plans/{planId}/milestones")
@Tag(name = "Learning Milestones", description = "Authenticated learning milestone APIs")
@SecurityRequirement(name = "bearer-jwt")
public class LearningMilestoneController {

  private final LearningMilestoneService learningMilestoneService;

  /**
   * Creates the learning milestone controller.
   *
   * @param learningMilestoneService milestone service
   */
  public LearningMilestoneController(LearningMilestoneService learningMilestoneService) {
    this.learningMilestoneService = learningMilestoneService;
  }

  /**
   * Creates a milestone under a plan.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param request create payload
   * @return created milestone
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create learning milestone")
  public ResponseEntity<ApiResponse<LearningMilestoneResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Valid @RequestBody LearningMilestoneRequest request) {
    LearningMilestoneResponse response =
        learningMilestoneService.create(principal.getId(), planId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a milestone under a plan.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @param request update payload
   * @return updated milestone
   */
  @PutMapping(
      path = "/{milestoneId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update learning milestone")
  public ResponseEntity<ApiResponse<LearningMilestoneResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId,
      @Valid @RequestBody LearningMilestoneRequest request) {
    LearningMilestoneResponse response =
        learningMilestoneService.update(principal.getId(), planId, milestoneId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a milestone under a plan.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @return success envelope
   */
  @DeleteMapping(path = "/{milestoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete learning milestone")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId) {
    learningMilestoneService.delete(principal.getId(), planId, milestoneId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a milestone with topics and progress.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @return milestone details
   */
  @GetMapping(path = "/{milestoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get learning milestone")
  public ResponseEntity<ApiResponse<LearningMilestoneResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId) {
    LearningMilestoneResponse response =
        learningMilestoneService.get(principal.getId(), planId, milestoneId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists milestones for a plan.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @return milestones
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List learning milestones")
  public ResponseEntity<ApiResponse<List<LearningMilestoneResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId) {
    List<LearningMilestoneResponse> response =
        learningMilestoneService.list(principal.getId(), planId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
