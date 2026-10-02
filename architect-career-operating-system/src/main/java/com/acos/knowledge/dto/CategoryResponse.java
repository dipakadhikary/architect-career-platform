package com.acos.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import java.util.UUID;

/**
 * Category details embedded in a knowledge note response.
 *
 * @param id category id
 * @param name category name
 * @param description optional description
 */
@Schema(name = "CategoryResponse", description = "Knowledge category summary")
public record CategoryResponse(
    @Schema(
            description = "Category identifier",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Category name",
            example = "System Design",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(
            description = "Optional description",
            example = "Architecture interview topics",
            nullable = true)
        String description) {

  /**
   * Creates an immutable category response.
   *
   * @param id category id
   * @param name category name
   * @param description optional description
   */
  public CategoryResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
  }
}
