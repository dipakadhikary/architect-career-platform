package com.acos.portfolio.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.portfolio.dto.PortfolioProjectPageResponse;
import com.acos.portfolio.dto.PortfolioProjectRequest;
import com.acos.portfolio.dto.PortfolioProjectResponse;
import com.acos.portfolio.service.PortfolioProjectService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST API for portfolio project management. */
@RestController
@RequestMapping("/api/v1/portfolio/projects")
@Tag(name = "Portfolio Projects", description = "Authenticated portfolio project APIs")
@SecurityRequirement(name = "bearer-jwt")
public class PortfolioProjectController {

  private final PortfolioProjectService portfolioProjectService;

  /**
   * Creates the portfolio project controller.
   *
   * @param portfolioProjectService project service
   */
  public PortfolioProjectController(PortfolioProjectService portfolioProjectService) {
    this.portfolioProjectService = portfolioProjectService;
  }

  /**
   * Creates a portfolio project for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created project
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create portfolio project")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Project created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProjectApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<PortfolioProjectResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody PortfolioProjectRequest request) {
    PortfolioProjectResponse response = portfolioProjectService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a portfolio project owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param projectId project id
   * @param request update payload
   * @return updated project
   */
  @PutMapping(
      path = "/{projectId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update portfolio project")
  public ResponseEntity<ApiResponse<PortfolioProjectResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Portfolio project identifier") @PathVariable UUID projectId,
      @Valid @RequestBody PortfolioProjectRequest request) {
    PortfolioProjectResponse response =
        portfolioProjectService.update(principal.getId(), projectId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a portfolio project owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param projectId project id
   * @return success envelope
   */
  @DeleteMapping(path = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete portfolio project")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Portfolio project identifier") @PathVariable UUID projectId) {
    portfolioProjectService.delete(principal.getId(), projectId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a portfolio project owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param projectId project id
   * @return project details
   */
  @GetMapping(path = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get portfolio project")
  public ResponseEntity<ApiResponse<PortfolioProjectResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Portfolio project identifier") @PathVariable UUID projectId) {
    PortfolioProjectResponse response = portfolioProjectService.get(principal.getId(), projectId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists portfolio projects for the authenticated user.
   *
   * @param principal authenticated principal
   * @param pageable paging and sorting
   * @return page of projects
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List portfolio projects")
  public ResponseEntity<ApiResponse<PortfolioProjectPageResponse>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    PortfolioProjectPageResponse response =
        portfolioProjectService.list(principal.getId(), pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Searches portfolio projects by title or summary for the authenticated user.
   *
   * @param principal authenticated principal
   * @param query search text
   * @param pageable paging and sorting
   * @return page of matching projects
   */
  @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Search portfolio projects")
  public ResponseEntity<ApiResponse<PortfolioProjectPageResponse>> search(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Search text matched against title or summary", required = true)
          @RequestParam("q")
          String query,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    PortfolioProjectPageResponse response =
        portfolioProjectService.search(principal.getId(), query, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful project envelope.
   *
   * @param success whether the request succeeded
   * @param data project payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "PortfolioProjectApiResponse",
      description = "Successful portfolio project envelope")
  public record ProjectApiResponse(
      boolean success,
      PortfolioProjectResponse data,
      ApiError error,
      String correlationId,
      Instant timestamp) {}

  /**
   * OpenAPI schema for a portfolio error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on failure
   * @param error error payload
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "PortfolioErrorApiResponse", description = "Portfolio error response envelope")
  public record PortfolioErrorApiResponse(
      boolean success, Void data, ApiError error, String correlationId, Instant timestamp) {}
}
