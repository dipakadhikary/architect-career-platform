package com.acos.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

/**
 * Paginated portfolio projects.
 *
 * @param content page items
 * @param page zero-based page index
 * @param size page size
 * @param totalElements total matching elements
 * @param totalPages total pages
 * @param first whether this is the first page
 * @param last whether this is the last page
 */
@Schema(name = "PortfolioProjectPageResponse", description = "Paginated portfolio projects")
public record PortfolioProjectPageResponse(
    @Schema(description = "Page items", requiredMode = Schema.RequiredMode.REQUIRED)
        List<PortfolioProjectResponse> content,
    @Schema(
            description = "Zero-based page index",
            example = "0",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int page,
    @Schema(description = "Page size", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int size,
    @Schema(
            description = "Total matching elements",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long totalElements,
    @Schema(description = "Total pages", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int totalPages,
    @Schema(
            description = "Whether this is the first page",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean first,
    @Schema(
            description = "Whether this is the last page",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean last) {

  /**
   * Creates an immutable page response.
   *
   * @param content page items
   * @param page page index
   * @param size page size
   * @param totalElements total elements
   * @param totalPages total pages
   * @param first first-page flag
   * @param last last-page flag
   */
  public PortfolioProjectPageResponse {
    Objects.requireNonNull(content, "content must not be null");
    content = List.copyOf(content);
  }
}
