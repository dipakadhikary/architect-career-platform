package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Payload used to create or update a job application. Status is managed exclusively through the
 * dedicated status transition endpoint; new applications always start in {@code DRAFT}.
 *
 * @param companyId target company id
 * @param recruiterId optional recruiter id
 * @param title job title
 * @param jobDescription optional job description
 * @param source optional application source
 * @param salaryExpectation optional salary expectation
 * @param currency optional currency code
 * @param resumeVersion optional resume version identifier
 * @param appliedOn application date
 * @param location optional location
 * @param jobUrl optional job posting URL
 * @param notes optional notes
 */
@Schema(
    name = "JobApplicationRequest",
    description = "Payload used to create or update a job application")
public record JobApplicationRequest(
    @Schema(description = "Target company identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "companyId must not be null") UUID companyId,
    @Schema(description = "Optional recruiter identifier", nullable = true) UUID recruiterId,
    @Schema(
            description = "Job title",
            example = "Staff Software Architect",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(description = "Optional job description", maxLength = 10_000, nullable = true)
        @Size(max = 10_000, message = "jobDescription must not exceed 10000 characters") String jobDescription,
    @Schema(
            description = "Optional application source",
            example = "LinkedIn",
            maxLength = 100,
            nullable = true)
        @Size(max = 100, message = "source must not exceed 100 characters") String source,
    @Schema(description = "Optional salary expectation", example = "220000.00", nullable = true)
        BigDecimal salaryExpectation,
    @Schema(
            description = "Optional ISO currency code",
            example = "USD",
            maxLength = 3,
            nullable = true)
        @Size(max = 3, message = "currency must not exceed 3 characters") String currency,
    @Schema(
            description = "Optional resume version identifier",
            example = "v3-architect",
            maxLength = 100,
            nullable = true)
        @Size(max = 100, message = "resumeVersion must not exceed 100 characters") String resumeVersion,
    @Schema(
            description = "Application date",
            example = "2026-08-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "appliedOn must not be null") LocalDate appliedOn,
    @Schema(description = "Optional location", example = "Remote", maxLength = 200, nullable = true)
        @Size(max = 200, message = "location must not exceed 200 characters") String location,
    @Schema(
            description = "Optional job posting URL",
            example = "https://jobs.example.com/123",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "jobUrl must not exceed 500 characters") String jobUrl,
    @Schema(
            description = "Optional notes",
            example = "Referred by former colleague",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "notes must not exceed 2000 characters") String notes) {}
