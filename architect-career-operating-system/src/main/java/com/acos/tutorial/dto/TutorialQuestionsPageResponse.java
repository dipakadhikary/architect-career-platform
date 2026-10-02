package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "TutorialQuestionsPageResponse")
public record TutorialQuestionsPageResponse(
    String title,
    String path,
    List<TutorialBreadcrumbItem> breadcrumb,
    List<TutorialQuestionResponse> questions) {}
