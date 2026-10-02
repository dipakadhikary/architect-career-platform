#!/usr/bin/env python3
"""Generate tutorial DTOs, repos, utils, exceptions."""
from pathlib import Path

BASE = Path(r"D:\architect-career-system\architect-career-operating-system\src\main\java\com\acos\tutorial")


def w(rel: str, content: str) -> None:
    path = BASE / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")
    print(rel)


w("dto/TutorialTopicRequest.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(name = "TutorialTopicRequest")
public record TutorialTopicRequest(
    @NotBlank @Size(max = 200) @Schema(example = "Design Patterns") String title,
    @Size(max = 200) @Schema(description = "Optional URL slug; generated from title when blank") String slug,
    @Schema(description = "Parent topic id; null for root") UUID parentId,
    @Schema(description = "Display order among siblings; auto when null") Integer sortOrder) {}
""")

w("dto/TutorialTopicResponse.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
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
    Instant createdAt,
    Instant updatedAt) {}
""")

w("dto/TutorialTreeNodeResponse.java", """
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
""")

w("dto/TutorialConceptRequest.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "TutorialConceptRequest")
public record TutorialConceptRequest(
    @NotBlank @Size(max = 100_000) @Schema(description = "Markdown concept body") String content) {}
""")

w("dto/TutorialConceptResponse.java", """
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
""")

w("dto/TutorialBreadcrumbItem.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "TutorialBreadcrumbItem")
public record TutorialBreadcrumbItem(UUID id, String title, String slug, String path) {}
""")

w("dto/TutorialQuestionRequest.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "TutorialQuestionRequest")
public record TutorialQuestionRequest(
    @NotBlank @Size(max = 50_000) String question,
    @NotBlank @Size(max = 50_000) String answer,
    Integer sortOrder) {}
""")

w("dto/TutorialQuestionResponse.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "TutorialQuestionResponse")
public record TutorialQuestionResponse(
    UUID id, UUID topicId, String question, String answer, int sortOrder, Instant updatedAt) {}
""")

w("dto/TutorialQuestionsPageResponse.java", """
package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "TutorialQuestionsPageResponse")
public record TutorialQuestionsPageResponse(
    String title,
    String path,
    List<TutorialBreadcrumbItem> breadcrumb,
    List<TutorialQuestionResponse> questions) {}
""")

w("dto/TutorialSearchResultResponse.java", """
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
""")

w("dto/TutorialSearchPageResponse.java", """
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
""")

w("util/TutorialSlugger.java", """
package com.acos.tutorial.util;

import java.util.Locale;
import java.util.Objects;

/** Generates URL-safe tutorial slugs. */
public final class TutorialSlugger {

  private TutorialSlugger() {}

  public static String slugify(String title) {
    Objects.requireNonNull(title, "title");
    String slug =
        title
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    if (slug.isBlank()) {
      throw new IllegalArgumentException("Unable to derive slug from title");
    }
    if (slug.length() > 200) {
      slug = slug.substring(0, 200).replaceAll("-+$", "");
    }
    return slug;
  }
}
""")

w("exception/TutorialTopicNotFoundException.java", """
package com.acos.tutorial.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

public class TutorialTopicNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public TutorialTopicNotFoundException(UUID topicId) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "TutorialTopic not found with identifier '" + topicId + "'");
  }

  public TutorialTopicNotFoundException(String path) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "TutorialTopic not found with path '" + path + "'");
  }
}
""")

w("exception/TutorialQuestionNotFoundException.java", """
package com.acos.tutorial.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

public class TutorialQuestionNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public TutorialQuestionNotFoundException(UUID questionId) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "TutorialQuestion not found with identifier '" + questionId + "'");
  }
}
""")

w("exception/TutorialCircularHierarchyException.java", """
package com.acos.tutorial.exception;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import java.util.List;

public class TutorialCircularHierarchyException extends ValidationException {

  private static final long serialVersionUID = 1L;

  public TutorialCircularHierarchyException() {
    super(
        "Circular tutorial hierarchy is not allowed",
        List.of(ApiError.FieldErrorDetail.ofField("parentId", "would create a cycle")));
  }
}
""")

w("exception/TutorialDuplicatePathException.java", """
package com.acos.tutorial.exception;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import java.util.List;

public class TutorialDuplicatePathException extends ValidationException {

  private static final long serialVersionUID = 1L;

  public TutorialDuplicatePathException(String path) {
    super(
        "Tutorial path already exists: " + path,
        List.of(ApiError.FieldErrorDetail.ofField("path", "must be unique for the owner")));
  }
}
""")

print("dto/util/ex done")
