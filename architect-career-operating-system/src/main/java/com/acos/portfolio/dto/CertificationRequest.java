package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Payload used to create or update a certification.
 *
 * @param name certification name
 * @param issuer issuing organization
 * @param credentialId optional credential identifier
 * @param credentialUrl optional credential verification URL
 * @param issuedOn issue date
 * @param expiresOn optional expiry date
 */
@Schema(
    name = "CertificationRequest",
    description = "Payload used to create or update a certification")
public record CertificationRequest(
    @Schema(
            description = "Certification name",
            example = "AWS Solutions Architect Associate",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "name must not be blank") @Size(max = 200, message = "name must not exceed 200 characters") String name,
    @Schema(
            description = "Issuing organization",
            example = "Amazon Web Services",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "issuer must not be blank") @Size(max = 200, message = "issuer must not exceed 200 characters") String issuer,
    @Schema(
            description = "Optional credential identifier",
            example = "AWS-123456",
            maxLength = 200,
            nullable = true)
        @Size(max = 200, message = "credentialId must not exceed 200 characters") String credentialId,
    @Schema(
            description = "Optional credential verification URL",
            example = "https://www.credly.com/badges/example",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "credentialUrl must not exceed 500 characters") String credentialUrl,
    @Schema(
            description = "Issue date",
            example = "2024-05-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "issuedOn must not be null") LocalDate issuedOn,
    @Schema(description = "Optional expiry date", example = "2027-05-01", nullable = true)
        LocalDate expiresOn) {}
