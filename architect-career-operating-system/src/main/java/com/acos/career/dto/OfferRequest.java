package com.acos.career.dto;

import com.acos.career.entity.OfferStatus;
import com.acos.career.entity.WorkMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload used to create or update an offer. On creation the offer always starts as {@code
 * PENDING}; the {@code offerStatus} field is honored only for updates.
 *
 * @param baseSalary base salary
 * @param currency currency code
 * @param joiningBonus optional joining bonus
 * @param annualBonus optional annual bonus
 * @param stockOptions optional stock option details
 * @param location optional work location
 * @param workMode optional work mode
 * @param joiningDate optional joining date
 * @param noticePeriodDays optional notice period in days
 * @param offerStatus offer status, honored only for updates
 * @param offerExpiryDate optional offer expiry date
 * @param notes optional notes
 */
@Schema(name = "OfferRequest", description = "Payload used to create or update an offer")
public record OfferRequest(
    @Schema(
            description = "Base salary",
            example = "230000.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "baseSalary must not be null") BigDecimal baseSalary,
    @Schema(
            description = "ISO currency code",
            example = "USD",
            maxLength = 3,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "currency must not be blank") @Size(min = 3, max = 3, message = "currency must be exactly 3 characters") String currency,
    @Schema(description = "Optional joining bonus", example = "10000.00", nullable = true)
        BigDecimal joiningBonus,
    @Schema(description = "Optional annual bonus", example = "20000.00", nullable = true)
        BigDecimal annualBonus,
    @Schema(
            description = "Optional stock option details",
            example = "15,000 RSUs over 4 years",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "stockOptions must not exceed 500 characters") String stockOptions,
    @Schema(
            description = "Optional work location",
            example = "Remote",
            maxLength = 200,
            nullable = true)
        @Size(max = 200, message = "location must not exceed 200 characters") String location,
    @Schema(description = "Optional work mode", example = "REMOTE", nullable = true)
        WorkMode workMode,
    @Schema(description = "Optional joining date", example = "2026-10-01", nullable = true)
        LocalDate joiningDate,
    @Schema(description = "Optional notice period in days", example = "30", nullable = true)
        @PositiveOrZero(message = "noticePeriodDays must not be negative") Integer noticePeriodDays,
    @Schema(
            description = "Offer status, honored only on update",
            example = "PENDING",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "offerStatus must not be null") OfferStatus offerStatus,
    @Schema(description = "Optional offer expiry date", example = "2026-09-15", nullable = true)
        LocalDate offerExpiryDate,
    @Schema(
            description = "Optional notes",
            example = "Negotiating start date",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "notes must not exceed 2000 characters") String notes) {}
