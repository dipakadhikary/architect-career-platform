package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response containing a portfolio review.
 *
 * @param summary overall review summary
 * @param strengths portfolio strengths
 * @param improvements suggested improvements
 * @param score overall score from 0 to 100
 */
@Schema(name = "PortfolioReviewResponse", description = "Portfolio review results")
public record PortfolioReviewResponse(
    @Schema(description = "Overall review summary") String summary,
    @Schema(description = "Portfolio strengths") List<String> strengths,
    @Schema(description = "Suggested improvements") List<String> improvements,
    @Schema(description = "Overall score from 0 to 100", example = "81.0") Double score) {}
