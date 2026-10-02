package com.acos.portfolio.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.portfolio.dto.TechnologyRequest;
import com.acos.portfolio.dto.TechnologyResponse;
import com.acos.portfolio.service.TechnologyService;
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

/** REST API for portfolio technology catalog management. */
@RestController
@RequestMapping("/api/v1/portfolio/technologies")
@Tag(name = "Portfolio Technologies", description = "Authenticated portfolio technology APIs")
@SecurityRequirement(name = "bearer-jwt")
public class TechnologyController {

  private final TechnologyService technologyService;

  /**
   * Creates the technology controller.
   *
   * @param technologyService technology service
   */
  public TechnologyController(TechnologyService technologyService) {
    this.technologyService = technologyService;
  }

  /**
   * Creates a technology for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created technology
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create technology")
  public ResponseEntity<ApiResponse<TechnologyResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody TechnologyRequest request) {
    TechnologyResponse response = technologyService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a technology owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param technologyId technology id
   * @param request update payload
   * @return updated technology
   */
  @PutMapping(
      path = "/{technologyId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update technology")
  public ResponseEntity<ApiResponse<TechnologyResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Technology identifier") @PathVariable UUID technologyId,
      @Valid @RequestBody TechnologyRequest request) {
    TechnologyResponse response =
        technologyService.update(principal.getId(), technologyId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a technology owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param technologyId technology id
   * @return success envelope
   */
  @DeleteMapping(path = "/{technologyId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete technology")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Technology identifier") @PathVariable UUID technologyId) {
    technologyService.delete(principal.getId(), technologyId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a technology owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param technologyId technology id
   * @return technology details
   */
  @GetMapping(path = "/{technologyId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get technology")
  public ResponseEntity<ApiResponse<TechnologyResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Technology identifier") @PathVariable UUID technologyId) {
    TechnologyResponse response = technologyService.get(principal.getId(), technologyId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists technologies for the authenticated user.
   *
   * @param principal authenticated principal
   * @return technologies
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List technologies")
  public ResponseEntity<ApiResponse<List<TechnologyResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal) {
    List<TechnologyResponse> response = technologyService.list(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
