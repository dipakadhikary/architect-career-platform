package com.acos.career.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Career tracker configuration limits.
 *
 * @param maxPageSize maximum allowed page size for list APIs
 * @param maxNotesLength maximum allowed notes length
 * @param maxJobDescriptionLength maximum allowed job description length
 */
@ConfigurationProperties(prefix = "acos.career")
public record CareerProperties(int maxPageSize, int maxNotesLength, int maxJobDescriptionLength) {

  /**
   * Creates career properties with validation.
   *
   * @param maxPageSize max page size
   * @param maxNotesLength max notes length
   * @param maxJobDescriptionLength max job description length
   */
  public CareerProperties {
    if (maxPageSize <= 0) {
      throw new IllegalArgumentException("maxPageSize must be positive");
    }
    if (maxNotesLength <= 0) {
      throw new IllegalArgumentException("maxNotesLength must be positive");
    }
    if (maxJobDescriptionLength <= 0) {
      throw new IllegalArgumentException("maxJobDescriptionLength must be positive");
    }
  }
}
