package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response containing a learning progress evaluation.
 *
 * @param progressPercent overall progress from 0 to 100
 * @param summary evaluation summary
 * @param strengths observed strengths
 * @param focusAreas areas needing focus
 */
@Schema(name = "EvaluateProgressResponse", description = "Learning progress evaluation")
public record EvaluateProgressResponse(
    @Schema(description = "Overall progress from 0 to 100", example = "62.5")
        Double progressPercent,
    @Schema(description = "Evaluation summary") String summary,
    @Schema(description = "Observed strengths") List<String> strengths,
    @Schema(description = "Areas needing focus") List<String> focusAreas) {}
