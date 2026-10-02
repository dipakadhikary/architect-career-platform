package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Response containing knowledge search hits.
 *
 * @param hits ranked search results
 */
@Schema(name = "KnowledgeSearchResponse", description = "Ranked knowledge search results")
public record KnowledgeSearchResponse(
    @Schema(description = "Ranked search results") List<KnowledgeSearchHit> hits) {

  /**
   * A single knowledge search hit.
   *
   * @param noteId matched knowledge note identifier
   * @param title matched document title
   * @param snippet excerpt highlighting the match
   * @param score relevance score
   */
  @Schema(name = "KnowledgeSearchHit", description = "A single knowledge search hit")
  public record KnowledgeSearchHit(
      @Schema(description = "Matched knowledge note identifier") UUID noteId,
      @Schema(description = "Matched document title") String title,
      @Schema(description = "Excerpt highlighting the match") String snippet,
      @Schema(description = "Relevance score") double score) {}
}
