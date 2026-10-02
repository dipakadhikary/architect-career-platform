package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to analyze skill gaps for a target role.
 *
 * @param userId owning user identifier
 * @param targetRole target role title
 * @param currentSkills skills the user currently has
 * @param projectTechnologies technologies demonstrated in portfolio projects
 */
@Schema(name = "SkillGapRequest", description = "Request to analyze skill gaps")
public record SkillGapRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(
            description = "Target role title",
            example = "Principal Architect",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String targetRole,
    @Schema(description = "Skills the user currently has") List<String> currentSkills,
    @Schema(description = "Technologies demonstrated in portfolio projects")
        List<String> projectTechnologies) {}
