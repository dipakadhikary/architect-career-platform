package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "TutorialQuestionRequest")
public record TutorialQuestionRequest(
    @NotBlank @Size(max = 50_000) String question,
    @NotBlank @Size(max = 50_000) String answer,
    Integer sortOrder) {}
