package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(name = "TutorialTreeNodeResponse")
public record TutorialTreeNodeResponse(
    UUID id,
    String title,
    String slug,
    String path,
    int sortOrder,
    boolean hasConcept,
    boolean hasQuestions,
    List<TutorialTreeNodeResponse> children) {}
