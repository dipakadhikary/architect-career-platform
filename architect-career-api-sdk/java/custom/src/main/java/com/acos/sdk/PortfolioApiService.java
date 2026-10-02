package com.acos.sdk;

import com.acos.sdk.generated.ApiClient;
import com.acos.sdk.generated.api.PortfolioAchievementsApi;
import com.acos.sdk.generated.api.PortfolioCertificationsApi;
import com.acos.sdk.generated.api.PortfolioProjectsApi;
import com.acos.sdk.generated.api.PortfolioSkillsApi;
import com.acos.sdk.generated.api.PortfolioTechnologiesApi;
import com.acos.sdk.generated.model.ApiResponsePortfolioProjectPageResponse;
import com.acos.sdk.generated.model.ApiResponsePortfolioProjectResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain wrapper for Portfolio generated APIs.
 */
public final class PortfolioApiService extends ServiceSupport {

  private final PortfolioProjectsApi projects;
  private final PortfolioSkillsApi skills;
  private final PortfolioTechnologiesApi technologies;
  private final PortfolioCertificationsApi certifications;
  private final PortfolioAchievementsApi achievements;

  public PortfolioApiService(ApiClient apiClient) {
    this(apiClient, RetryPolicy.defaults());
  }

  public PortfolioApiService(ApiClient apiClient, RetryPolicy retryPolicy) {
    super(retryPolicy, PortfolioApiService.class);
    Objects.requireNonNull(apiClient, "apiClient");
    this.projects = new PortfolioProjectsApi(apiClient);
    this.skills = new PortfolioSkillsApi(apiClient);
    this.technologies = new PortfolioTechnologiesApi(apiClient);
    this.certifications = new PortfolioCertificationsApi(apiClient);
    this.achievements = new PortfolioAchievementsApi(apiClient);
  }

  public PortfolioProjectsApi projects() {
    return projects;
  }

  public PortfolioSkillsApi skills() {
    return skills;
  }

  public PortfolioTechnologiesApi technologies() {
    return technologies;
  }

  public PortfolioCertificationsApi certifications() {
    return certifications;
  }

  public PortfolioAchievementsApi achievements() {
    return achievements;
  }

  public ApiResponsePortfolioProjectResponse getProject(UUID projectId) {
    return execute("portfolio.getProject", () -> projects.get2(projectId));
  }

  public ApiResponsePortfolioProjectPageResponse listProjects(
      Integer page, Integer size, List<String> sort) {
    return execute("portfolio.listProjects", () -> projects.list2(page, size, sort));
  }
}
