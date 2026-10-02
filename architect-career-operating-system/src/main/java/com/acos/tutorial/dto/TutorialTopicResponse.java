package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(name = "TutorialTopicResponse")
public record TutorialTopicResponse(
    UUID id,
    UUID parentId,
    String title,
    String slug,
    String path,
    int sortOrder,
    boolean hasConcept,
    boolean hasQuestions,
    int childCount,
    List<TutorialBreadcrumbItem> breadcrumb,
    Instant createdAt,
    Instant updatedAt) {}
