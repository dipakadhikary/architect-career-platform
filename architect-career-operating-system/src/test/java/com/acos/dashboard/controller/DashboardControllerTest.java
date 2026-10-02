package com.acos.dashboard.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.handler.GlobalExceptionHandler;
import com.acos.dashboard.dto.DashboardResponse;
import com.acos.dashboard.service.DashboardService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** MockMvc slice tests for {@link DashboardController}. */
@WebMvcTest(controllers = DashboardController.class)
@Import({
  GlobalExceptionHandler.class,
  DashboardControllerTest.PermitAllSecurityConfiguration.class
})
class DashboardControllerTest {

  private static final String DASHBOARD_PATH = "/api/v1/dashboard";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private DashboardService dashboardService;

  @Test
  void shouldReturnDashboardForAuthenticatedUser() throws Exception {
    DashboardResponse response =
        new DashboardResponse("Welcome to ACOS, " + EMAIL, 45, 2, 3, 1, 4, 2);
    when(dashboardService.getDashboard(EMAIL)).thenReturn(response);

    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));

    mockMvc
        .perform(
            get(DASHBOARD_PATH)
                .with(
                    authentication(
                        new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.welcomeMessage").value("Welcome to ACOS, " + EMAIL))
        .andExpect(jsonPath("$.data.profileCompletion").value(45))
        .andExpect(jsonPath("$.data.activeLearningPlans").value(2))
        .andExpect(jsonPath("$.data.completedCourses").value(3))
        .andExpect(jsonPath("$.data.portfolioProjects").value(1))
        .andExpect(jsonPath("$.data.jobApplications").value(4))
        .andExpect(jsonPath("$.data.upcomingInterviews").value(2));

    verify(dashboardService).getDashboard(EMAIL);
  }

  /** Permissive security for controller slice tests; JWT enforcement is covered by IT. */
  static class PermitAllSecurityConfiguration {

    @Bean
    SecurityFilterChain permitAllSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .sessionManagement(
              session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
      return http.build();
    }
  }
}
