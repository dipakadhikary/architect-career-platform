package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Response containing a knowledge summary.
 *
 * @param noteId optional source note identifier
 * @param summary generated summary text
 * @param keyPoints extracted key points
 */
@Schema(name = "KnowledgeSummarizeResponse", description = "Knowledge summary payload")
public record KnowledgeSummarizeResponse(
    @Schema(description = "Optional source note identifier") UUID noteId,
    @Schema(description = "Generated summary text") String summary,
    @Schema(description = "Extracted key points") List<String> keyPoints) {}
