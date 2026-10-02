package com.acos.learning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Payload used to create or update a learning milestone.
 *
 * @param title milestone title
 * @param description optional description
 * @param sortOrder optional sort order; when null on create, appended at the end
 * @param targetDate optional target date
 */
@Schema(
    name = "LearningMilestoneRequest",
    description = "Payload used to create or update a learning milestone")
public record LearningMilestoneRequest(
    @Schema(
            description = "Milestone title",
            example = "Consistency Models",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(
            description = "Optional description",
            example = "Cover linearizability, sequential consistency, and eventual consistency",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "description must not exceed 2000 characters") String description,
    @Schema(
            description = "Optional sort order; omitted on create appends at the end",
            example = "1",
            nullable = true)
        Integer sortOrder,
    @Schema(description = "Optional target date", example = "2026-09-30", nullable = true)
        LocalDate targetDate) {}
