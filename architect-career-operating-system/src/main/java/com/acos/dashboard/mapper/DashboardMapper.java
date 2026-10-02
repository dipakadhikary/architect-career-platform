package com.acos.dashboard.mapper;

import com.acos.dashboard.config.DashboardProperties;
import com.acos.dashboard.dto.DashboardResponse;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Maps authenticated-user context and placeholder metrics into a dashboard response. */
@Component
public class DashboardMapper {

  private final DashboardProperties properties;

  /**
   * Creates the dashboard mapper.
   *
   * @param properties dashboard placeholder properties
   */
  public DashboardMapper(DashboardProperties properties) {
    this.properties = properties;
  }

  /**
   * Builds a dashboard response for the authenticated user email.
   *
   * @param email authenticated user email
   * @return dashboard response
   */
  public DashboardResponse toResponse(String email) {
    Objects.requireNonNull(email, "email must not be null");
    return new DashboardResponse(
        properties.welcomeMessagePrefix() + email,
        properties.profileCompletion(),
        properties.activeLearningPlans(),
        properties.completedCourses(),
        properties.portfolioProjects(),
        properties.jobApplications(),
        properties.upcomingInterviews());
  }
}
