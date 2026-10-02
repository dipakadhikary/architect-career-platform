package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing a generated resume.
 *
 * @param content generated resume body
 * @param format content format (for example {@code markdown} or {@code plain})
 */
@Schema(name = "ResumeResponse", description = "Generated resume payload")
public record ResumeResponse(
    @Schema(description = "Generated resume body") String content,
    @Schema(description = "Content format", example = "markdown") String format) {}
