package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing a generated cover letter.
 *
 * @param content generated cover letter body
 * @param format content format (for example {@code markdown} or {@code plain})
 */
@Schema(name = "CoverLetterResponse", description = "Generated cover letter payload")
public record CoverLetterResponse(
    @Schema(description = "Generated cover letter body") String content,
    @Schema(description = "Content format", example = "markdown") String format) {}
