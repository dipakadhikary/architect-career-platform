package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to review a portfolio.
 *
 * @param userId owning user identifier
 * @param projectIds portfolio project identifiers to review
 * @param targetRole optional target role for the review
 */
@Schema(name = "PortfolioReviewRequest", description = "Request to review a portfolio")
public record PortfolioReviewRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(description = "Portfolio project identifiers to review") List<UUID> projectIds,
    @Schema(description = "Optional target role for the review", example = "Solutions Architect")
        String targetRole) {}
