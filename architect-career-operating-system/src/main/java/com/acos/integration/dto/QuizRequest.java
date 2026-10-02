package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Request to generate a quiz.
 *
 * @param userId owning user identifier
 * @param topic quiz topic
 * @param difficulty difficulty label
 * @param questionCount number of questions to generate
 */
@Schema(name = "QuizRequest", description = "Request to generate a quiz")
public record QuizRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(
            description = "Quiz topic",
            example = "Distributed systems",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String topic,
    @Schema(description = "Difficulty label", example = "INTERMEDIATE") String difficulty,
    @Schema(description = "Number of questions to generate", example = "5")
        Integer questionCount) {}
