package com.acos.knowledge.validator;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import com.acos.knowledge.config.KnowledgeProperties;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/** Validates and normalizes knowledge note request fields beyond Bean Validation. */
@Component
public class KnowledgeNoteValidator {

  private static final int MAX_TAG_NAME_LENGTH = 50;

  private final KnowledgeProperties properties;

  /**
   * Creates the validator.
   *
   * @param properties knowledge limits
   */
  public KnowledgeNoteValidator(KnowledgeProperties properties) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
  }

  /**
   * Validates markdown content length.
   *
   * @param content markdown body
   */
  public void validateContent(String content) {
    Objects.requireNonNull(content, "content must not be null");
    if (content.length() > properties.maxContentLength()) {
      throw new ValidationException(
          "content must not exceed " + properties.maxContentLength() + " characters",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "content",
                  "content must not exceed " + properties.maxContentLength() + " characters")));
    }
  }

  /**
   * Normalizes and validates tag names.
   *
   * @param tagNames raw tag names, may be {@code null}
   * @return normalized unique tag names, or empty when {@code tagNames} is {@code null}
   */
  public Set<String> normalizeTagNames(List<String> tagNames) {
    if (tagNames == null) {
      return Set.of();
    }
    Set<String> normalized = new LinkedHashSet<>();
    List<ApiError.FieldErrorDetail> details = new ArrayList<>();
    for (int index = 0; index < tagNames.size(); index++) {
      normalizeSingleTag(tagNames.get(index), index, normalized, details);
    }
    if (!details.isEmpty()) {
      throw new ValidationException("tagNames validation failed", details);
    }
    ensureTagLimit(normalized);
    return Set.copyOf(normalized);
  }

  /**
   * Normalizes an optional category name.
   *
   * @param categoryName raw category name
   * @return trimmed name, or {@code null} when blank
   */
  public String normalizeCategoryName(String categoryName) {
    if (categoryName == null || categoryName.isBlank()) {
      return null;
    }
    return categoryName.trim();
  }

  /**
   * Validates a search query.
   *
   * @param query search text
   * @return trimmed query
   */
  public String normalizeSearchQuery(String query) {
    if (query == null || query.isBlank()) {
      throw new ValidationException(
          "search query must not be blank",
          List.of(ApiError.FieldErrorDetail.ofField("q", "search query must not be blank")));
    }
    return query.trim();
  }

  /**
   * Validates pageable size against configured limits.
   *
   * @param pageable requested pageable
   */
  public void validatePageable(Pageable pageable) {
    Objects.requireNonNull(pageable, "pageable must not be null");
    if (pageable.getPageSize() > properties.maxPageSize()) {
      throw new ValidationException(
          "page size must not exceed " + properties.maxPageSize(),
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "size", "page size must not exceed " + properties.maxPageSize())));
    }
  }

  private void normalizeSingleTag(
      String raw, int index, Set<String> normalized, List<ApiError.FieldErrorDetail> details) {
    if (raw == null || raw.isBlank()) {
      details.add(
          ApiError.FieldErrorDetail.ofField(
              "tagNames[" + index + "]", "tag name must not be blank"));
      return;
    }
    String value = raw.trim().toLowerCase(Locale.ROOT);
    if (value.length() > MAX_TAG_NAME_LENGTH) {
      details.add(
          ApiError.FieldErrorDetail.ofField(
              "tagNames[" + index + "]",
              "tag name must not exceed " + MAX_TAG_NAME_LENGTH + " characters"));
      return;
    }
    normalized.add(value);
  }

  private void ensureTagLimit(Set<String> normalized) {
    if (normalized.size() > properties.maxTagsPerNote()) {
      throw new ValidationException(
          "A note may have at most " + properties.maxTagsPerNote() + " tags",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "tagNames", "A note may have at most " + properties.maxTagsPerNote() + " tags")));
    }
  }
}
