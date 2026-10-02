package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Request to generate a cover letter.
 *
 * @param userId owning user identifier
 * @param targetRole desired role title
 * @param companyName target company name
 * @param highlights experience highlights to emphasize
 */
@Schema(name = "CoverLetterRequest", description = "Request to generate a cover letter")
public record CoverLetterRequest(
    @Schema(description = "Owning user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,
    @Schema(
            description = "Desired role title",
            example = "Staff Software Architect",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String targetRole,
    @Schema(description = "Target company name", example = "Acme Corp") String companyName,
    @Schema(description = "Experience highlights to emphasize") List<String> highlights) {}
