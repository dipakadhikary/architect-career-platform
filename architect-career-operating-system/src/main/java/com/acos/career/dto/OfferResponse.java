package com.acos.career.dto;

import com.acos.career.entity.OfferStatus;
import com.acos.career.entity.WorkMode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Offer returned by the career APIs.
 *
 * @param id offer id
 * @param applicationId owning application id
 * @param baseSalary base salary
 * @param currency currency code
 * @param joiningBonus optional joining bonus
 * @param annualBonus optional annual bonus
 * @param stockOptions optional stock option details
 * @param location optional work location
 * @param workMode optional work mode
 * @param joiningDate optional joining date
 * @param noticePeriodDays optional notice period in days
 * @param offerStatus offer status
 * @param offerExpiryDate optional offer expiry date
 * @param notes optional notes
 * @param archived whether the offer has been archived
 * @param archivedAt optional archival timestamp
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 * @param version optimistic locking version
 */
@Schema(name = "OfferResponse", description = "Job offer")
public record OfferResponse(
    @Schema(description = "Offer identifier", requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
    @Schema(
            description = "Owning application identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID applicationId,
    @Schema(description = "Base salary", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal baseSalary,
    @Schema(description = "Currency code", requiredMode = Schema.RequiredMode.REQUIRED)
        String currency,
    @Schema(description = "Optional joining bonus", nullable = true) BigDecimal joiningBonus,
    @Schema(description = "Optional annual bonus", nullable = true) BigDecimal annualBonus,
    @Schema(description = "Optional stock option details", nullable = true) String stockOptions,
    @Schema(description = "Optional work location", nullable = true) String location,
    @Schema(description = "Optional work mode", nullable = true) WorkMode workMode,
    @Schema(description = "Optional joining date", nullable = true) LocalDate joiningDate,
    @Schema(description = "Optional notice period in days", nullable = true)
        Integer noticePeriodDays,
    @Schema(description = "Offer status", requiredMode = Schema.RequiredMode.REQUIRED)
        OfferStatus offerStatus,
    @Schema(description = "Optional offer expiry date", nullable = true) LocalDate offerExpiryDate,
    @Schema(description = "Optional notes", nullable = true) String notes,
    @Schema(
            description = "Whether the offer is archived",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean archived,
    @Schema(description = "Optional archival timestamp", nullable = true) Instant archivedAt,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt,
    @Schema(description = "Optimistic locking version", requiredMode = Schema.RequiredMode.REQUIRED)
        long version) {

  /**
   * Creates an immutable offer response.
   *
   * @param id offer id
   * @param applicationId application id
   * @param baseSalary base salary
   * @param currency currency
   * @param joiningBonus joining bonus
   * @param annualBonus annual bonus
   * @param stockOptions stock options
   * @param location location
   * @param workMode work mode
   * @param joiningDate joining date
   * @param noticePeriodDays notice period days
   * @param offerStatus offer status
   * @param offerExpiryDate offer expiry date
   * @param notes notes
   * @param archived archived flag
   * @param archivedAt archived-at timestamp
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   * @param version optimistic locking version
   */
  public OfferResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(baseSalary, "baseSalary must not be null");
    Objects.requireNonNull(currency, "currency must not be null");
    Objects.requireNonNull(offerStatus, "offerStatus must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
