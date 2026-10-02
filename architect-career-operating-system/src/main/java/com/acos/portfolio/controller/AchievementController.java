package com.acos.portfolio.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.portfolio.dto.AchievementRequest;
import com.acos.portfolio.dto.AchievementResponse;
import com.acos.portfolio.service.AchievementService;
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

/** REST API for portfolio achievement management. */
@RestController
@RequestMapping("/api/v1/portfolio/achievements")
@Tag(name = "Portfolio Achievements", description = "Authenticated portfolio achievement APIs")
@SecurityRequirement(name = "bearer-jwt")
public class AchievementController {

  private final AchievementService achievementService;

  /**
   * Creates the achievement controller.
   *
   * @param achievementService achievement service
   */
  public AchievementController(AchievementService achievementService) {
    this.achievementService = achievementService;
  }

  /**
   * Creates an achievement for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created achievement
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create achievement")
  public ResponseEntity<ApiResponse<AchievementResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody AchievementRequest request) {
    AchievementResponse response = achievementService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates an achievement owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param achievementId achievement id
   * @param request update payload
   * @return updated achievement
   */
  @PutMapping(
      path = "/{achievementId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update achievement")
  public ResponseEntity<ApiResponse<AchievementResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Achievement identifier") @PathVariable UUID achievementId,
      @Valid @RequestBody AchievementRequest request) {
    AchievementResponse response =
        achievementService.update(principal.getId(), achievementId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes an achievement owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param achievementId achievement id
   * @return success envelope
   */
  @DeleteMapping(path = "/{achievementId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete achievement")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Achievement identifier") @PathVariable UUID achievementId) {
    achievementService.delete(principal.getId(), achievementId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns an achievement owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param achievementId achievement id
   * @return achievement details
   */
  @GetMapping(path = "/{achievementId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get achievement")
  public ResponseEntity<ApiResponse<AchievementResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Achievement identifier") @PathVariable UUID achievementId) {
    AchievementResponse response = achievementService.get(principal.getId(), achievementId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists achievements for the authenticated user.
   *
   * @param principal authenticated principal
   * @return achievements
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List achievements")
  public ResponseEntity<ApiResponse<List<AchievementResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal) {
    List<AchievementResponse> response = achievementService.list(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
