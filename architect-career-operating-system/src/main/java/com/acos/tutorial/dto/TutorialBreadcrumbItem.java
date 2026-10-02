package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "TutorialBreadcrumbItem")
public record TutorialBreadcrumbItem(UUID id, String title, String slug, String path) {}
