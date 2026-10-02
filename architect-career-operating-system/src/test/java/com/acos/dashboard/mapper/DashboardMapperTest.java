package com.acos.dashboard.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.dashboard.config.DashboardProperties;
import com.acos.dashboard.dto.DashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DashboardMapper}. */
class DashboardMapperTest {

  private static final String EMAIL = "ada@acos.local";

  private DashboardMapper dashboardMapper;

  @BeforeEach
  void setUp() {
    dashboardMapper =
        new DashboardMapper(new DashboardProperties("Welcome to ACOS, ", 45, 2, 3, 1, 4, 2));
  }

  @Test
  void shouldMapPlaceholderMetricsForAuthenticatedEmail() {
    DashboardResponse response = dashboardMapper.toResponse(EMAIL);

    assertThat(response.welcomeMessage()).isEqualTo("Welcome to ACOS, " + EMAIL);
    assertThat(response.profileCompletion()).isEqualTo(45);
    assertThat(response.activeLearningPlans()).isEqualTo(2);
    assertThat(response.completedCourses()).isEqualTo(3);
    assertThat(response.portfolioProjects()).isEqualTo(1);
    assertThat(response.jobApplications()).isEqualTo(4);
    assertThat(response.upcomingInterviews()).isEqualTo(2);
  }

  @Test
  void shouldRejectNullEmail() {
    assertThatThrownBy(() -> dashboardMapper.toResponse(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("email");
  }
}
