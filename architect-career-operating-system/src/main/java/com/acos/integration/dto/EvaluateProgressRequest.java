package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to evaluate learning progress.
 *
 * @param userId owning user identifier
 * @param planId optional learning plan identifier
 * @param completedTopics completed topics
 * @param quizScores recent quiz scores from 0 to 100
 */
@Schema(name = "EvaluateProgressRequest", description = "Request to evaluate learning progress")
public record EvaluateProgressRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(description = "Optional learning plan identifier") UUID planId,
    @Schema(description = "Completed topics") List<String> completedTopics,
    @Schema(description = "Recent quiz scores from 0 to 100") List<Double> quizScores) {}
