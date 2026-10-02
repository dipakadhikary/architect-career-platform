package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Achievement returned by the portfolio APIs.
 *
 * @param id achievement id
 * @param title achievement title
 * @param description achievement description
 * @param achievedOn date achieved
 * @param organization optional organization
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "AchievementResponse", description = "Portfolio achievement")
public record AchievementResponse(
    @Schema(description = "Achievement identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Achievement title", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(description = "Achievement description", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
    @Schema(description = "Date achieved", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate achievedOn,
    @Schema(description = "Optional organization", nullable = true) String organization,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable achievement response.
   *
   * @param id achievement id
   * @param title title
   * @param description description
   * @param achievedOn achieved on date
   * @param organization organization
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public AchievementResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(description, "description must not be null");
    Objects.requireNonNull(achievedOn, "achievedOn must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
