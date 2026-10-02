package com.acos.dashboard.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.testsupport.AuthApiTestSupport;
import com.acos.testsupport.PostgresTestSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

/** End-to-end integration tests for the authenticated dashboard API. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardIntegrationTest {

  private static final String DASHBOARD_PATH = "/api/v1/dashboard";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldReturnPlaceholderDashboardForAuthenticatedUser() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("dashboard");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    mockMvc
        .perform(get(DASHBOARD_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.welcomeMessage").value("Welcome to ACOS, " + email))
        .andExpect(jsonPath("$.data.profileCompletion").value(45))
        .andExpect(jsonPath("$.data.activeLearningPlans").value(2))
        .andExpect(jsonPath("$.data.completedCourses").value(3))
        .andExpect(jsonPath("$.data.portfolioProjects").value(1))
        .andExpect(jsonPath("$.data.jobApplications").value(4))
        .andExpect(jsonPath("$.data.upcomingInterviews").value(2))
        .andExpect(jsonPath("$.error").doesNotExist());
  }

  @Test
  void shouldRejectDashboardWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get(DASHBOARD_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }
}
