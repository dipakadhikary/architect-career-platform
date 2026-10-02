package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to generate a resume tailored to a target role.
 *
 * @param userId owning user identifier
 * @param targetRole desired role title
 * @param experienceHighlights notable experience points
 * @param skills skill keywords to emphasize
 */
@Schema(name = "ResumeRequest", description = "Request to generate a tailored resume")
public record ResumeRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(
            description = "Desired role title",
            example = "Staff Software Architect",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String targetRole,
    @Schema(description = "Notable experience points") List<String> experienceHighlights,
    @Schema(description = "Skill keywords to emphasize") List<String> skills) {}
