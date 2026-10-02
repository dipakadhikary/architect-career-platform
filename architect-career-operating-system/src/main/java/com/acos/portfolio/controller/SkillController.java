package com.acos.portfolio.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.portfolio.dto.SkillRequest;
import com.acos.portfolio.dto.SkillResponse;
import com.acos.portfolio.service.SkillService;
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

/** REST API for portfolio skill management. */
@RestController
@RequestMapping("/api/v1/portfolio/skills")
@Tag(name = "Portfolio Skills", description = "Authenticated portfolio skill APIs")
@SecurityRequirement(name = "bearer-jwt")
public class SkillController {

  private final SkillService skillService;

  /**
   * Creates the skill controller.
   *
   * @param skillService skill service
   */
  public SkillController(SkillService skillService) {
    this.skillService = skillService;
  }

  /**
   * Creates a skill for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created skill
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create skill")
  public ResponseEntity<ApiResponse<SkillResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody SkillRequest request) {
    SkillResponse response = skillService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a skill owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param skillId skill id
   * @param request update payload
   * @return updated skill
   */
  @PutMapping(
      path = "/{skillId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update skill")
  public ResponseEntity<ApiResponse<SkillResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Skill identifier") @PathVariable UUID skillId,
      @Valid @RequestBody SkillRequest request) {
    SkillResponse response = skillService.update(principal.getId(), skillId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a skill owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param skillId skill id
   * @return success envelope
   */
  @DeleteMapping(path = "/{skillId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete skill")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Skill identifier") @PathVariable UUID skillId) {
    skillService.delete(principal.getId(), skillId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a skill owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param skillId skill id
   * @return skill details
   */
  @GetMapping(path = "/{skillId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get skill")
  public ResponseEntity<ApiResponse<SkillResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Skill identifier") @PathVariable UUID skillId) {
    SkillResponse response = skillService.get(principal.getId(), skillId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists skills for the authenticated user.
   *
   * @param principal authenticated principal
   * @return skills
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List skills")
  public ResponseEntity<ApiResponse<List<SkillResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal) {
    List<SkillResponse> response = skillService.list(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
