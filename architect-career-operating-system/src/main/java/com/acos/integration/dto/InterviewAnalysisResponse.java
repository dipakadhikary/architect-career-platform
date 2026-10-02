package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response containing interview analysis results.
 *
 * @param summary overall analysis summary
 * @param strengths observed strengths
 * @param improvements suggested improvements
 * @param score overall score from 0 to 100
 */
@Schema(name = "InterviewAnalysisResponse", description = "Interview analysis results")
public record InterviewAnalysisResponse(
    @Schema(description = "Overall analysis summary") String summary,
    @Schema(description = "Observed strengths") List<String> strengths,
    @Schema(description = "Suggested improvements") List<String> improvements,
    @Schema(description = "Overall score from 0 to 100", example = "78.5") Double score) {}
