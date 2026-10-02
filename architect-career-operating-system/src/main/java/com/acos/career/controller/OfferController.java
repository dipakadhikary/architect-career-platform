package com.acos.career.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.OfferRequest;
import com.acos.career.dto.OfferResponse;
import com.acos.career.service.OfferService;
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

/** REST API for career offer management. */
@RestController
@RequestMapping("/api/v1/career/applications/{applicationId}/offers")
@Tag(name = "Career Offers", description = "Authenticated career offer APIs")
@SecurityRequirement(name = "bearer-jwt")
public class OfferController {

  private final OfferService offerService;

  /**
   * Creates the offer controller.
   *
   * @param offerService offer service
   */
  public OfferController(OfferService offerService) {
    this.offerService = offerService;
  }

  /**
   * Creates an offer under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param request create payload
   * @return created offer
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Create offer",
      description =
          "Creates an offer under a job application owned by the authenticated user. Only one"
              + " active (non-archived) offer is allowed per application.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Offer created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = OfferApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Offer request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for offer APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Owning job application was not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Pending offer already exists or application is archived",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<OfferResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Valid @RequestBody OfferRequest request) {
    OfferResponse response = offerService.create(principal.getId(), applicationId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates an offer under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param offerId offer id
   * @param request update payload
   * @return updated offer
   */
  @PutMapping(
      path = "/{offerId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Update offer",
      description =
          "Updates an offer under a job application owned by the authenticated user. Accepting or"
              + " declining an offer may synchronize the application status when allowed.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Offer updated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = OfferApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Offer request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for offer APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Offer not found under the authenticated user application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "Archived applications cannot manage offers",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<OfferResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Parameter(description = "Offer identifier", required = true) @PathVariable UUID offerId,
      @Valid @RequestBody OfferRequest request) {
    OfferResponse response =
        offerService.update(principal.getId(), applicationId, offerId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes an offer under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param offerId offer id
   * @return success envelope
   */
  @DeleteMapping(path = "/{offerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Delete offer",
      description = "Deletes an offer under a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Offer deleted",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema =
                    @Schema(implementation = CompanyController.CareerDeleteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for offer APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Offer not found under the authenticated user application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Parameter(description = "Offer identifier", required = true) @PathVariable UUID offerId) {
    offerService.delete(principal.getId(), applicationId, offerId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns an offer under a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @param offerId offer id
   * @return offer details
   */
  @GetMapping(path = "/{offerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get offer",
      description = "Returns an offer under a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Offer found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = OfferApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for offer APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Offer not found under the authenticated user application",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<OfferResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId,
      @Parameter(description = "Offer identifier", required = true) @PathVariable UUID offerId) {
    OfferResponse response = offerService.get(principal.getId(), applicationId, offerId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists offers for a job application.
   *
   * @param principal authenticated principal
   * @param applicationId application id
   * @return offers
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List offers",
      description = "Lists offers for a job application owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Offers listed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = OfferListApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "JWT authentication required for offer APIs",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Owning job application was not found for the authenticated user",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CompanyController.CareerErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<List<OfferResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Job application identifier", required = true) @PathVariable
          UUID applicationId) {
    List<OfferResponse> response = offerService.list(principal.getId(), applicationId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful offer envelope.
   *
   * @param success whether the request succeeded
   * @param data offer payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "CareerOfferApiResponse", description = "Successful career offer envelope")
  public record OfferApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Offer payload") OfferResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful offer list envelope.
   *
   * @param success whether the request succeeded
   * @param data offers payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "CareerOfferListApiResponse",
      description = "Successful career offer list envelope")
  public record OfferListApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Offers payload") List<OfferResponse> data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
