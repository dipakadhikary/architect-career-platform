package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to recommend the next learning topic.
 *
 * @param userId owning user identifier
 * @param planId optional learning plan identifier
 * @param completedTopics topics already completed
 * @param goals learning goals
 */
@Schema(
    name = "RecommendNextTopicRequest",
    description = "Request to recommend the next learning topic")
public record RecommendNextTopicRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(description = "Optional learning plan identifier") UUID planId,
    @Schema(description = "Topics already completed") List<String> completedTopics,
    @Schema(description = "Learning goals") List<String> goals) {}
