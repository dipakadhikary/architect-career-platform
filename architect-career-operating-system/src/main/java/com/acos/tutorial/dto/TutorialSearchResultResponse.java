package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(name = "TutorialSearchResultResponse")
public record TutorialSearchResultResponse(
    UUID topicId,
    String title,
    String path,
    List<TutorialBreadcrumbItem> breadcrumb,
    String snippet,
    String contentType,
    double rank) {}
