package com.acos.career.dto;

import com.acos.career.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A single recorded application status transition.
 *
 * @param id history entry id
 * @param oldStatus previous status, {@code null} for the initial entry
 * @param newStatus resulting status
 * @param changedAt transition timestamp
 * @param changedBy user id who performed the transition
 * @param comments optional comments
 * @param archived whether the history entry has been archived
 * @param archivedAt optional archival timestamp
 */
@Schema(name = "ApplicationStatusHistoryResponse", description = "Recorded status transition")
public record ApplicationStatusHistoryResponse(
    @Schema(description = "History entry identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Previous status", nullable = true) ApplicationStatus oldStatus,
    @Schema(description = "Resulting status", requiredMode = Schema.RequiredMode.REQUIRED)
        ApplicationStatus newStatus,
    @Schema(description = "Transition timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant changedAt,
    @Schema(
            description = "User id who performed the transition",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID changedBy,
    @Schema(description = "Optional comments", nullable = true) String comments,
    @Schema(
            description = "Whether the history entry is archived",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean archived,
    @Schema(description = "Optional archival timestamp", nullable = true) Instant archivedAt) {

  /**
   * Creates an immutable status history response.
   *
   * @param id history entry id
   * @param oldStatus previous status
   * @param newStatus resulting status
   * @param changedAt transition timestamp
   * @param changedBy actor id
   * @param comments comments
   * @param archived archived flag
   * @param archivedAt archived-at timestamp
   */
  public ApplicationStatusHistoryResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(newStatus, "newStatus must not be null");
    Objects.requireNonNull(changedAt, "changedAt must not be null");
    Objects.requireNonNull(changedBy, "changedBy must not be null");
  }
}
