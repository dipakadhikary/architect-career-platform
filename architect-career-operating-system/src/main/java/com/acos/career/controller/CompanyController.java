package com.acos.career.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.CompanyRequest;
import com.acos.career.dto.CompanyResponse;
import com.acos.career.service.CompanyService;
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

/** REST API for career company management. */
@RestController
@RequestMapping("/api/v1/career/companies")
@Tag(name = "Career Companies", description = "Authenticated career company APIs")
@SecurityRequirement(name = "bearer-jwt")
public class CompanyController {

  private final CompanyService companyService;

  /**
   * Creates the company controller.
   *
   * @param companyService company service
   */
  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  /**
   * Creates a company for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created company
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Create company",
      description =
          "Creates a tracked company owned by the authenticated user. Duplicate names for the"
              + " same owner are rejected.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Company created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Company request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for company APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Business rule violation (for example duplicate company name)",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<CompanyResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody CompanyRequest request) {
    CompanyResponse response = companyService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a company owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param companyId company id
   * @param request update payload
   * @return updated company
   */
  @PutMapping(
      path = "/{companyId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Update company",
      description = "Updates a company owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Company updated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Company request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for company APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Company not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Business rule violation",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<CompanyResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Company identifier", required = true) @PathVariable UUID companyId,
      @Valid @RequestBody CompanyRequest request) {
    CompanyResponse response = companyService.update(principal.getId(), companyId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a company owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param companyId company id
   * @return success envelope
   */
  @DeleteMapping(path = "/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Delete company",
      description =
          "Deletes a company owned by the authenticated user. Companies referenced by job"
              + " applications cannot be deleted.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Company deleted",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerDeleteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for company APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Company not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Company is still referenced by job applications",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Company identifier", required = true) @PathVariable
          UUID companyId) {
    companyService.delete(principal.getId(), companyId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a company owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param companyId company id
   * @return company details
   */
  @GetMapping(path = "/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get company",
      description = "Returns a company owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Company found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for company APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Company not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<CompanyResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Company identifier", required = true) @PathVariable
          UUID companyId) {
    CompanyResponse response = companyService.get(principal.getId(), companyId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists companies for the authenticated user.
   *
   * @param principal authenticated principal
   * @return companies
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List companies",
      description = "Lists all companies owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Companies listed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyListApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for company APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<List<CompanyResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal) {
    List<CompanyResponse> response = companyService.list(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful company envelope.
   *
   * @param success whether the request succeeded
   * @param data company payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerCompanyApiResponse", description = "Successful career company envelope")
  public record CompanyApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Company payload") CompanyResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful company list envelope.
   *
   * @param success whether the request succeeded
   * @param data companies payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerCompanyListApiResponse",
      description = "Successful career company list envelope")
  public record CompanyListApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Companies payload") List<CompanyResponse> data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful delete envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on delete success
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerDeleteApiResponse", description = "Successful career delete envelope")
  public record CareerDeleteApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a career error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on failure
   * @param error error payload
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerErrorApiResponse", description = "Career error response envelope")
  public record CareerErrorApiResponse(
      @Schema(description = "Whether the request succeeded", example = "false") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload") ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
