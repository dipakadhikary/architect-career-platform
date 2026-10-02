package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Request to analyze an interview transcript.
 *
 * @param userId owning user identifier
 * @param interviewId related interview identifier
 * @param transcript interview transcript text
 * @param jobDescription optional target job description
 */
@Schema(name = "InterviewAnalysisRequest", description = "Request to analyze an interview")
public record InterviewAnalysisRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(description = "Related interview identifier") UUID interviewId,
    @Schema(description = "Interview transcript text", requiredMode = Schema.RequiredMode.REQUIRED)
        String transcript,
    @Schema(description = "Optional target job description") String jobDescription) {}
