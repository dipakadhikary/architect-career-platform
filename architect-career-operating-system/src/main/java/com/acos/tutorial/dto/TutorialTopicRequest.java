package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(name = "TutorialTopicRequest")
public record TutorialTopicRequest(
    @NotBlank @Size(max = 200) @Schema(example = "Design Patterns") String title,
    @Size(max = 200) @Schema(description = "Optional URL slug; generated from title when blank")
        String slug,
    @Schema(description = "Parent topic id; null for root") UUID parentId,
    @Schema(description = "Display order among siblings; auto when null") Integer sortOrder) {}
