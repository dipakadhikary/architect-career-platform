package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Company returned by the career APIs.
 *
 * @param id company id
 * @param name company name
 * @param website optional website
 * @param industry optional industry
 * @param location optional location
 * @param notes optional notes
 * @param archived whether the company has been archived
 * @param archivedAt optional archival timestamp
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 * @param version optimistic locking version
 */
@Schema(name = "CompanyResponse", description = "Tracked company")
public record CompanyResponse(
    @Schema(description = "Company identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Company name", requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(description = "Optional website", nullable = true) String website,
    @Schema(description = "Optional industry", nullable = true) String industry,
    @Schema(description = "Optional location", nullable = true) String location,
    @Schema(description = "Optional notes", nullable = true) String notes,
    @Schema(
            description = "Whether the company is archived",
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
   * Creates an immutable company response.
   *
   * @param id company id
   * @param name name
   * @param website website
   * @param industry industry
   * @param location location
   * @param notes notes
   * @param archived archived flag
   * @param archivedAt archived-at timestamp
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   * @param version optimistic locking version
   */
  public CompanyResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
