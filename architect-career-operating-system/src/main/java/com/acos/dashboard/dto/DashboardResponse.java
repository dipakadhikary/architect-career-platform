package com.acos.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * Dashboard summary returned to an authenticated user.
 *
 * <p>Metric fields are placeholder values until analytics aggregation is implemented.
 *
 * @param welcomeMessage personalized welcome message
 * @param profileCompletion profile completion percentage (0-100)
 * @param activeLearningPlans number of active learning plans
 * @param completedCourses number of completed courses
 * @param portfolioProjects number of portfolio projects
 * @param jobApplications number of job applications
 * @param upcomingInterviews number of upcoming interviews
 */
@Schema(name = "DashboardResponse", description = "Authenticated user dashboard summary")
public record DashboardResponse(
    @Schema(
            description = "Personalized welcome message",
            example = "Welcome to ACOS, ada@acos.local",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String welcomeMessage,
    @Schema(
            description = "Profile completion percentage from 0 to 100",
            example = "45",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int profileCompletion,
    @Schema(
            description = "Active learning plans",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int activeLearningPlans,
    @Schema(
            description = "Completed courses",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int completedCourses,
    @Schema(
            description = "Portfolio projects",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int portfolioProjects,
    @Schema(
            description = "Job applications",
            example = "4",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int jobApplications,
    @Schema(
            description = "Upcoming interviews",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int upcomingInterviews) {

  /**
   * Creates an immutable dashboard response.
   *
   * @param welcomeMessage welcome message
   * @param profileCompletion profile completion percentage
   * @param activeLearningPlans active learning plans
   * @param completedCourses completed courses
   * @param portfolioProjects portfolio projects
   * @param jobApplications job applications
   * @param upcomingInterviews upcoming interviews
   */
  public DashboardResponse {
    Objects.requireNonNull(welcomeMessage, "welcomeMessage must not be null");
  }
}
