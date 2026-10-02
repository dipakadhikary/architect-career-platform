package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Certification returned by the portfolio APIs.
 *
 * @param id certification id
 * @param name certification name
 * @param issuer issuing organization
 * @param credentialId optional credential identifier
 * @param credentialUrl optional credential verification URL
 * @param issuedOn issue date
 * @param expiresOn optional expiry date
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "CertificationResponse", description = "Portfolio certification")
public record CertificationResponse(
    @Schema(description = "Certification identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Certification name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(description = "Issuing organization", requiredMode = Schema.RequiredMode.REQUIRED)
        String issuer,
    @Schema(description = "Optional credential identifier", nullable = true) String credentialId,
    @Schema(description = "Optional credential verification URL", nullable = true)
        String credentialUrl,
    @Schema(description = "Issue date", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate issuedOn,
    @Schema(description = "Optional expiry date", nullable = true) LocalDate expiresOn,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable certification response.
   *
   * @param id certification id
   * @param name name
   * @param issuer issuer
   * @param credentialId credential id
   * @param credentialUrl credential URL
   * @param issuedOn issue date
   * @param expiresOn expiry date
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public CertificationResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(issuer, "issuer must not be null");
    Objects.requireNonNull(issuedOn, "issuedOn must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
