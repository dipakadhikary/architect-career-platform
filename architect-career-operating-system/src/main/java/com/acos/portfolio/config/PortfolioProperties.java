package com.acos.portfolio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Portfolio configuration limits.
 *
 * @param maxPageSize maximum allowed page size for list and search APIs
 * @param maxTechnologiesPerProject maximum technologies assignable to a single project
 * @param maxDescriptionLength maximum allowed project description length
 */
@ConfigurationProperties(prefix = "acos.portfolio")
public record PortfolioProperties(
    int maxPageSize, int maxTechnologiesPerProject, int maxDescriptionLength) {

  /**
   * Creates portfolio properties with validation.
   *
   * @param maxPageSize max page size
   * @param maxTechnologiesPerProject max technologies per project
   * @param maxDescriptionLength max description length
   */
  public PortfolioProperties {
    if (maxPageSize <= 0) {
      throw new IllegalArgumentException("maxPageSize must be positive");
    }
    if (maxTechnologiesPerProject <= 0) {
      throw new IllegalArgumentException("maxTechnologiesPerProject must be positive");
    }
    if (maxDescriptionLength <= 0) {
      throw new IllegalArgumentException("maxDescriptionLength must be positive");
    }
  }
}
