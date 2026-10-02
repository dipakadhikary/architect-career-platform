package com.acos.portfolio.dto;

import com.acos.portfolio.entity.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Payload used to create or update a portfolio project.
 *
 * @param title project title
 * @param summary short summary
 * @param description detailed description
 * @param repositoryUrl optional source repository URL
 * @param liveUrl optional live demo URL
 * @param status project status
 * @param startDate optional start date
 * @param endDate optional end date
 * @param technologyNames optional technology names; on create {@code null} means none; on update
 *     {@code null} leaves technologies unchanged and an empty list clears them
 */
@Schema(
    name = "PortfolioProjectRequest",
    description = "Payload used to create or update a portfolio project")
public record PortfolioProjectRequest(
    @Schema(
            description = "Project title",
            example = "ACOS Platform",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(
            description = "Short summary",
            example = "Career operating system for architects",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "summary must not be blank") @Size(max = 500, message = "summary must not exceed 500 characters") String summary,
    @Schema(
            description = "Detailed description",
            example = "Spring Boot platform covering learning, knowledge, and portfolio.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "description must not be blank") String description,
    @Schema(
            description = "Optional source repository URL",
            example = "https://github.com/example/acos",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "repositoryUrl must not exceed 500 characters") String repositoryUrl,
    @Schema(
            description = "Optional live demo URL",
            example = "https://acos.example.com",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "liveUrl must not exceed 500 characters") String liveUrl,
    @Schema(
            description = "Project status",
            example = "PUBLISHED",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "status must not be null") ProjectStatus status,
    @Schema(description = "Optional project start date", example = "2025-01-01", nullable = true)
        LocalDate startDate,
    @Schema(description = "Optional project end date", example = "2026-06-30", nullable = true)
        LocalDate endDate,
    @Schema(
            description = "Optional technology names; created for the owner when missing",
            example = "[\"Java\", \"Spring Boot\"]",
            nullable = true)
        List<
                @NotBlank(message = "technology name must not be blank") @Size(max = 100, message = "technology name must not exceed 100 characters") String>
            technologyNames) {}
