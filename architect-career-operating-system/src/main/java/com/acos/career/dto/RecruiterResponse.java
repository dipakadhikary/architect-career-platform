package com.acos.career.dto;

import com.acos.career.entity.RecruiterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Recruiter returned by the career APIs.
 *
 * @param id recruiter id
 * @param companyId optional associated company id
 * @param fullName recruiter full name
 * @param email optional email
 * @param phone optional phone
 * @param linkedInUrl optional LinkedIn URL
 * @param lastContactDate optional last contact date
 * @param nextFollowUpDate optional next follow-up date
 * @param status relationship status
 * @param notes optional notes
 * @param archived whether the recruiter has been archived
 * @param archivedAt optional archival timestamp
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 * @param version optimistic locking version
 */
@Schema(name = "RecruiterResponse", description = "Tracked recruiter contact")
public record RecruiterResponse(
    @Schema(description = "Recruiter identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Optional associated company identifier", nullable = true) UUID companyId,
    @Schema(description = "Recruiter full name", requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,
    @Schema(description = "Optional email", nullable = true) String email,
    @Schema(description = "Optional phone", nullable = true) String phone,
    @Schema(description = "Optional LinkedIn URL", nullable = true) String linkedInUrl,
    @Schema(description = "Optional last contact date", nullable = true) LocalDate lastContactDate,
    @Schema(description = "Optional next follow-up date", nullable = true)
        LocalDate nextFollowUpDate,
    @Schema(description = "Relationship status", requiredMode = Schema.RequiredMode.REQUIRED)
        RecruiterStatus status,
    @Schema(description = "Optional notes", nullable = true) String notes,
    @Schema(
            description = "Whether the recruiter is archived",
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
   * Creates an immutable recruiter response.
   *
   * @param id recruiter id
   * @param companyId company id
   * @param fullName full name
   * @param email email
   * @param phone phone
   * @param linkedInUrl LinkedIn URL
   * @param lastContactDate last contact date
   * @param nextFollowUpDate next follow-up date
   * @param status status
   * @param notes notes
   * @param archived archived flag
   * @param archivedAt archived-at timestamp
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   * @param version optimistic locking version
   */
  public RecruiterResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(fullName, "fullName must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
