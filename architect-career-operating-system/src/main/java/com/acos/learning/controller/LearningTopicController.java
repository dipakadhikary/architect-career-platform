package com.acos.learning.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.learning.dto.LearningTopicRequest;
import com.acos.learning.dto.LearningTopicResponse;
import com.acos.learning.dto.TopicStatusUpdateRequest;
import com.acos.learning.service.LearningTopicService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for learning topic management. */
@RestController
@RequestMapping("/api/v1/learning/plans/{planId}/milestones/{milestoneId}/topics")
@Tag(name = "Learning Topics", description = "Authenticated learning topic APIs")
@SecurityRequirement(name = "bearer-jwt")
public class LearningTopicController {

  private final LearningTopicService learningTopicService;

  /**
   * Creates the learning topic controller.
   *
   * @param learningTopicService topic service
   */
  public LearningTopicController(LearningTopicService learningTopicService) {
    this.learningTopicService = learningTopicService;
  }

  /**
   * Creates a topic under a milestone.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @param request create payload
   * @return created topic
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create learning topic")
  public ResponseEntity<ApiResponse<LearningTopicResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId,
      @Valid @RequestBody LearningTopicRequest request) {
    LearningTopicResponse response =
        learningTopicService.create(principal.getId(), planId, milestoneId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a topic under a milestone.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @param topicId topic id
   * @param request update payload
   * @return updated topic
   */
  @PutMapping(
      path = "/{topicId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update learning topic")
  public ResponseEntity<ApiResponse<LearningTopicResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId,
      @Parameter(description = "Learning topic identifier") @PathVariable UUID topicId,
      @Valid @RequestBody LearningTopicRequest request) {
    LearningTopicResponse response =
        learningTopicService.update(principal.getId(), planId, milestoneId, topicId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a topic under a milestone.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @param topicId topic id
   * @return success envelope
   */
  @DeleteMapping(path = "/{topicId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete learning topic")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId,
      @Parameter(description = "Learning topic identifier") @PathVariable UUID topicId) {
    learningTopicService.delete(principal.getId(), planId, milestoneId, topicId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Updates only the status of a topic.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @param topicId topic id
   * @param request status payload
   * @return updated topic
   */
  @PatchMapping(
      path = "/{topicId}/status",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update learning topic status")
  public ResponseEntity<ApiResponse<LearningTopicResponse>> updateStatus(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId,
      @Parameter(description = "Learning topic identifier") @PathVariable UUID topicId,
      @Valid @RequestBody TopicStatusUpdateRequest request) {
    LearningTopicResponse response =
        learningTopicService.updateStatus(principal.getId(), planId, milestoneId, topicId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists topics for a milestone.
   *
   * @param principal authenticated principal
   * @param planId plan id
   * @param milestoneId milestone id
   * @return topics
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List learning topics")
  public ResponseEntity<ApiResponse<List<LearningTopicResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Learning plan identifier") @PathVariable UUID planId,
      @Parameter(description = "Learning milestone identifier") @PathVariable UUID milestoneId) {
    List<LearningTopicResponse> response =
        learningTopicService.list(principal.getId(), planId, milestoneId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
