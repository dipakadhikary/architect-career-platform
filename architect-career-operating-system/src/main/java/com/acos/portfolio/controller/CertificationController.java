package com.acos.portfolio.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.portfolio.dto.CertificationRequest;
import com.acos.portfolio.dto.CertificationResponse;
import com.acos.portfolio.service.CertificationService;
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

/** REST API for portfolio certification management. */
@RestController
@RequestMapping("/api/v1/portfolio/certifications")
@Tag(name = "Portfolio Certifications", description = "Authenticated portfolio certification APIs")
@SecurityRequirement(name = "bearer-jwt")
public class CertificationController {

  private final CertificationService certificationService;

  /**
   * Creates the certification controller.
   *
   * @param certificationService certification service
   */
  public CertificationController(CertificationService certificationService) {
    this.certificationService = certificationService;
  }

  /**
   * Creates a certification for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created certification
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create certification")
  public ResponseEntity<ApiResponse<CertificationResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody CertificationRequest request) {
    CertificationResponse response = certificationService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a certification owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param certificationId certification id
   * @param request update payload
   * @return updated certification
   */
  @PutMapping(
      path = "/{certificationId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update certification")
  public ResponseEntity<ApiResponse<CertificationResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Certification identifier") @PathVariable UUID certificationId,
      @Valid @RequestBody CertificationRequest request) {
    CertificationResponse response =
        certificationService.update(principal.getId(), certificationId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a certification owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param certificationId certification id
   * @return success envelope
   */
  @DeleteMapping(path = "/{certificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete certification")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Certification identifier") @PathVariable UUID certificationId) {
    certificationService.delete(principal.getId(), certificationId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a certification owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param certificationId certification id
   * @return certification details
   */
  @GetMapping(path = "/{certificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get certification")
  public ResponseEntity<ApiResponse<CertificationResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Certification identifier") @PathVariable UUID certificationId) {
    CertificationResponse response = certificationService.get(principal.getId(), certificationId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists certifications for the authenticated user.
   *
   * @param principal authenticated principal
   * @return certifications
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List certifications")
  public ResponseEntity<ApiResponse<List<CertificationResponse>>> list(
      @AuthenticationPrincipal AcosUserDetails principal) {
    List<CertificationResponse> response = certificationService.list(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
