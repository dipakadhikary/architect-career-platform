package com.acos.portfolio.dto;

import com.acos.portfolio.entity.ProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Payload used to create or update a skill.
 *
 * @param name skill name
 * @param proficiencyLevel proficiency level
 * @param yearsOfExperience optional years of experience
 * @param description optional description
 */
@Schema(name = "SkillRequest", description = "Payload used to create or update a skill")
public record SkillRequest(
    @Schema(
            description = "Skill name",
            example = "System Design",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "name must not be blank") @Size(max = 100, message = "name must not exceed 100 characters") String name,
    @Schema(
            description = "Proficiency level",
            example = "ADVANCED",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "proficiencyLevel must not be null") ProficiencyLevel proficiencyLevel,
    @Schema(description = "Optional years of experience", example = "5.5", nullable = true)
        BigDecimal yearsOfExperience,
    @Schema(
            description = "Optional description",
            example = "Distributed systems and interview preparation",
            maxLength = 1000,
            nullable = true)
        @Size(max = 1000, message = "description must not exceed 1000 characters") String description) {}
