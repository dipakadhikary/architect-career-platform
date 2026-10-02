package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(name = "TutorialConceptResponse")
public record TutorialConceptResponse(
    UUID topicId,
    String title,
    String path,
    List<TutorialBreadcrumbItem> breadcrumb,
    String content,
    Instant updatedAt) {}
