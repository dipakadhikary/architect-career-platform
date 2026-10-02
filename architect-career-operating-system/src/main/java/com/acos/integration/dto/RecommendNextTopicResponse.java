package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response containing the next topic recommendation.
 *
 * @param topic recommended topic title
 * @param rationale why this topic is recommended
 * @param relatedTopics related follow-up topics
 */
@Schema(name = "RecommendNextTopicResponse", description = "Next learning topic recommendation")
public record RecommendNextTopicResponse(
    @Schema(description = "Recommended topic title") String topic,
    @Schema(description = "Why this topic is recommended") String rationale,
    @Schema(description = "Related follow-up topics") List<String> relatedTopics) {}
