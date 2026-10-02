package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import java.util.UUID;

/**
 * Compact company summary embedded in application responses.
 *
 * @param id company id
 * @param name company name
 */
@Schema(name = "CompanySummaryResponse", description = "Compact company summary")
public record CompanySummaryResponse(
    @Schema(description = "Company identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Company name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name) {

  /**
   * Creates an immutable company summary.
   *
   * @param id company id
   * @param name company name
   */
  public CompanySummaryResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
  }
}
