package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "TutorialConceptRequest")
public record TutorialConceptRequest(
    @NotBlank @Size(max = 100_000) @Schema(description = "Markdown concept body") String content) {}
