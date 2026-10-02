package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response containing skill-gap analysis results.
 *
 * @param summary overall skill-gap summary
 * @param missingSkills skills missing for the target role
 * @param recommendedActions recommended next actions
 */
@Schema(name = "SkillGapResponse", description = "Skill-gap analysis results")
public record SkillGapResponse(
    @Schema(description = "Overall skill-gap summary") String summary,
    @Schema(description = "Skills missing for the target role") List<String> missingSkills,
    @Schema(description = "Recommended next actions") List<String> recommendedActions) {}
