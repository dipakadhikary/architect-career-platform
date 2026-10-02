package com.acos.career.dto;

import com.acos.career.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Job application returned by the career APIs.
 *
 * @param id application id
 * @param company company summary
 * @param recruiter optional recruiter summary
 * @param title job title
 * @param jobDescription optional job description
 * @param source optional source
 * @param status application status
 * @param salaryExpectation optional salary expectation
 * @param currency optional currency
 * @param resumeVersion optional resume version identifier
 * @param appliedOn application date
 * @param location optional location
 * @param jobUrl optional job URL
 * @param notes optional notes
 * @param archived whether the application has been archived
 * @param archivedAt optional archival timestamp
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 * @param version optimistic locking version
 */
@Schema(name = "JobApplicationResponse", description = "Tracked job application")
public record JobApplicationResponse(
    @Schema(description = "Application identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Company summary", requiredMode = Schema.RequiredMode.REQUIRED)
        CompanySummaryResponse company,
    @Schema(description = "Optional recruiter summary", nullable = true)
        RecruiterSummaryResponse recruiter,
    @Schema(description = "Job title", requiredMode = Schema.RequiredMode.REQUIRED) String title,
    @Schema(description = "Optional job description", nullable = true) String jobDescription,
    @Schema(description = "Optional source", nullable = true) String source,
    @Schema(description = "Application status", requiredMode = Schema.RequiredMode.REQUIRED)
        ApplicationStatus status,
    @Schema(description = "Optional salary expectation", nullable = true)
        BigDecimal salaryExpectation,
    @Schema(description = "Optional currency", nullable = true) String currency,
    @Schema(description = "Optional resume version identifier", nullable = true)
        String resumeVersion,
    @Schema(description = "Application date", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate appliedOn,
    @Schema(description = "Optional location", nullable = true) String location,
    @Schema(description = "Optional job URL", nullable = true) String jobUrl,
    @Schema(description = "Optional notes", nullable = true) String notes,
    @Schema(
            description = "Whether the application is archived",
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
   * Creates an immutable job application response.
   *
   * @param id application id
   * @param company company summary
   * @param recruiter recruiter summary
   * @param title job title
   * @param jobDescription job description
   * @param source source
   * @param status status
   * @param salaryExpectation salary expectation
   * @param currency currency
   * @param resumeVersion resume version identifier
   * @param appliedOn applied-on date
   * @param location location
   * @param jobUrl job URL
   * @param notes notes
   * @param archived archived flag
   * @param archivedAt archived-at timestamp
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   * @param version optimistic locking version
   */
  public JobApplicationResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(company, "company must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(appliedOn, "appliedOn must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
