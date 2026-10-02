package com.acos.knowledge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Knowledge management configuration limits.
 *
 * @param maxContentLength maximum markdown content length in characters
 * @param maxTagsPerNote maximum tags assignable to a single note
 * @param maxPageSize maximum allowed page size for list and search APIs
 */
@ConfigurationProperties(prefix = "acos.knowledge")
public record KnowledgeProperties(int maxContentLength, int maxTagsPerNote, int maxPageSize) {

  /**
   * Creates knowledge properties with validation.
   *
   * @param maxContentLength max content length
   * @param maxTagsPerNote max tags per note
   * @param maxPageSize max page size
   */
  public KnowledgeProperties {
    if (maxContentLength <= 0) {
      throw new IllegalArgumentException("maxContentLength must be positive");
    }
    if (maxTagsPerNote <= 0) {
      throw new IllegalArgumentException("maxTagsPerNote must be positive");
    }
    if (maxPageSize <= 0) {
      throw new IllegalArgumentException("maxPageSize must be positive");
    }
  }
}
