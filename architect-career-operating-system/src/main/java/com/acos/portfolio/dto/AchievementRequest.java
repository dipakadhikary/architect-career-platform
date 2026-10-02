package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Payload used to create or update an achievement.
 *
 * @param title achievement title
 * @param description achievement description
 * @param achievedOn date achieved
 * @param organization optional organization
 */
@Schema(
    name = "AchievementRequest",
    description = "Payload used to create or update an achievement")
public record AchievementRequest(
    @Schema(
            description = "Achievement title",
            example = "Led platform modernization",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(
            description = "Achievement description",
            example = "Migrated monolith to modular Spring services.",
            maxLength = 2000,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "description must not be blank") @Size(max = 2000, message = "description must not exceed 2000 characters") String description,
    @Schema(
            description = "Date achieved",
            example = "2025-11-15",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "achievedOn must not be null") LocalDate achievedOn,
    @Schema(
            description = "Optional organization",
            example = "ACME Corp",
            maxLength = 200,
            nullable = true)
        @Size(max = 200, message = "organization must not exceed 200 characters") String organization) {}
