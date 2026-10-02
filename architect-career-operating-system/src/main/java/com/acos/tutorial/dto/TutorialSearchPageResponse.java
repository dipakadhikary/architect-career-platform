package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "TutorialSearchPageResponse")
public record TutorialSearchPageResponse(
    String query,
    List<TutorialSearchResultResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages) {}
