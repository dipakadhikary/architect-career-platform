package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload used to create or update a company.
 *
 * @param name company name
 * @param website optional website
 * @param industry optional industry
 * @param location optional location
 * @param notes optional notes
 */
@Schema(name = "CompanyRequest", description = "Payload used to create or update a company")
public record CompanyRequest(
    @Schema(
            description = "Company name",
            example = "Acme Corp",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "name must not be blank") @Size(max = 200, message = "name must not exceed 200 characters") String name,
    @Schema(
            description = "Optional website",
            example = "https://acme.example.com",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "website must not exceed 500 characters") String website,
    @Schema(
            description = "Optional industry",
            example = "Technology",
            maxLength = 100,
            nullable = true)
        @Size(max = 100, message = "industry must not exceed 100 characters") String industry,
    @Schema(
            description = "Optional location",
            example = "San Francisco, CA",
            maxLength = 200,
            nullable = true)
        @Size(max = 200, message = "location must not exceed 200 characters") String location,
    @Schema(
            description = "Optional notes",
            example = "Enterprise customer focus",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "notes must not exceed 2000 characters") String notes) {}
