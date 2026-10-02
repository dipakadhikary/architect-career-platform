package com.acos.portfolio.dto;

import com.acos.portfolio.entity.ProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Skill returned by the portfolio APIs.
 *
 * @param id skill id
 * @param name skill name
 * @param proficiencyLevel proficiency level
 * @param yearsOfExperience optional years of experience
 * @param description optional description
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "SkillResponse", description = "Portfolio skill")
public record SkillResponse(
    @Schema(description = "Skill identifier", requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
    @Schema(description = "Skill name", requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(description = "Proficiency level", requiredMode = Schema.RequiredMode.REQUIRED)
        ProficiencyLevel proficiencyLevel,
    @Schema(description = "Optional years of experience", nullable = true)
        BigDecimal yearsOfExperience,
    @Schema(description = "Optional description", nullable = true) String description,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable skill response.
   *
   * @param id skill id
   * @param name name
   * @param proficiencyLevel proficiency level
   * @param yearsOfExperience years of experience
   * @param description description
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public SkillResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(proficiencyLevel, "proficiencyLevel must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
