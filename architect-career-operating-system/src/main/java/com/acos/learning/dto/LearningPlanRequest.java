package com.acos.learning.dto;

import com.acos.learning.entity.LearningPlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Payload used to create or update a learning plan.
 *
 * @param title plan title
 * @param description optional description
 * @param status plan status
 * @param targetDate optional target completion date
 */
@Schema(
    name = "LearningPlanRequest",
    description = "Payload used to create or update a learning plan")
public record LearningPlanRequest(
    @Schema(
            description = "Plan title",
            example = "System Design Mastery",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(
            description = "Optional description",
            example = "Twelve-week plan covering distributed systems fundamentals",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "description must not exceed 2000 characters") String description,
    @Schema(
            description = "Plan status",
            example = "ACTIVE",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "status must not be null") LearningPlanStatus status,
    @Schema(
            description = "Optional target completion date",
            example = "2026-12-31",
            nullable = true)
        LocalDate targetDate) {}
