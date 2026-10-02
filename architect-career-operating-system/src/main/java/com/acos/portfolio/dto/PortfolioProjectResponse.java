package com.acos.portfolio.dto;

import com.acos.portfolio.entity.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Portfolio project returned by the portfolio APIs.
 *
 * @param id project id
 * @param title project title
 * @param summary short summary
 * @param description detailed description
 * @param repositoryUrl optional repository URL
 * @param liveUrl optional live URL
 * @param status project status
 * @param startDate optional start date
 * @param endDate optional end date
 * @param technologies assigned technology names
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "PortfolioProjectResponse", description = "Portfolio showcase project")
public record PortfolioProjectResponse(
    @Schema(description = "Project identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Project title", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(description = "Short summary", requiredMode = Schema.RequiredMode.REQUIRED)
        String summary,
    @Schema(description = "Detailed description", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
    @Schema(description = "Optional repository URL", nullable = true) String repositoryUrl,
    @Schema(description = "Optional live URL", nullable = true) String liveUrl,
    @Schema(description = "Project status", requiredMode = Schema.RequiredMode.REQUIRED)
        ProjectStatus status,
    @Schema(description = "Optional start date", nullable = true) LocalDate startDate,
    @Schema(description = "Optional end date", nullable = true) LocalDate endDate,
    @Schema(
            description = "Assigned technology names",
            example = "[\"Java\", \"Spring Boot\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> technologies,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt) {

  /**
   * Creates an immutable project response.
   *
   * @param id project id
   * @param title title
   * @param summary summary
   * @param description description
   * @param repositoryUrl repository URL
   * @param liveUrl live URL
   * @param status status
   * @param startDate start date
   * @param endDate end date
   * @param technologies technology names
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   */
  public PortfolioProjectResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(summary, "summary must not be null");
    Objects.requireNonNull(description, "description must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(technologies, "technologies must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    technologies = List.copyOf(technologies);
  }
}
