package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Technology returned by the portfolio APIs.
 *
 * @param id technology id
 * @param name technology name
 * @param category optional category
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "TechnologyResponse", description = "Portfolio technology")
public record TechnologyResponse(
    @Schema(description = "Technology identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Technology name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(description = "Optional category", nullable = true) String category,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable technology response.
   *
   * @param id technology id
   * @param name name
   * @param category category
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public TechnologyResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
