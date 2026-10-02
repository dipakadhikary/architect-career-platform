package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import java.util.UUID;

/**
 * Compact recruiter summary embedded in application responses.
 *
 * @param id recruiter id
 * @param fullName recruiter full name
 */
@Schema(name = "RecruiterSummaryResponse", description = "Compact recruiter summary")
public record RecruiterSummaryResponse(
    @Schema(description = "Recruiter identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Recruiter full name", requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName) {

  /**
   * Creates an immutable recruiter summary.
   *
   * @param id recruiter id
   * @param fullName full name
   */
  public RecruiterSummaryResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(fullName, "fullName must not be null");
  }
}
