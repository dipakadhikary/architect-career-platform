package com.acos.career.dto;

import com.acos.career.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * Application count for a single status.
 *
 * @param status application status
 * @param count number of applications in that status
 */
@Schema(name = "ApplicationStatusCountResponse", description = "Application count by status")
public record ApplicationStatusCountResponse(
    @Schema(description = "Application status", requiredMode = Schema.RequiredMode.REQUIRED)
        ApplicationStatus status,
    @Schema(
            description = "Count of applications",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long count) {

  /**
   * Creates an immutable status count.
   *
   * @param status status
   * @param count count
   */
  public ApplicationStatusCountResponse {
    Objects.requireNonNull(status, "status must not be null");
  }
}
