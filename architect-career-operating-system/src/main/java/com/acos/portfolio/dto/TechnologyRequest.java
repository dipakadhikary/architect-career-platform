package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload used to create or update a technology.
 *
 * @param name technology name
 * @param category optional category
 */
@Schema(name = "TechnologyRequest", description = "Payload used to create or update a technology")
public record TechnologyRequest(
    @Schema(
            description = "Technology name",
            example = "Spring Boot",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "name must not be blank") @Size(max = 100, message = "name must not exceed 100 characters") String name,
    @Schema(
            description = "Optional category",
            example = "Backend",
            maxLength = 100,
            nullable = true)
        @Size(max = 100, message = "category must not exceed 100 characters") String category) {}
