package com.acos.learning.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.handler.GlobalExceptionHandler;
import com.acos.learning.dto.LearningPlanPageResponse;
import com.acos.learning.dto.LearningPlanRequest;
import com.acos.learning.dto.LearningPlanResponse;
import com.acos.learning.dto.LearningPlanSummaryResponse;
import com.acos.learning.entity.LearningPlanStatus;
import com.acos.learning.service.LearningPlanService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** MockMvc slice tests for {@link LearningPlanController}. */
@WebMvcTest(controllers = LearningPlanController.class)
@Import({
  GlobalExceptionHandler.class,
  LearningPlanControllerTest.PermitAllSecurityConfiguration.class
})
class LearningPlanControllerTest {

  private static final String BASE_PATH = "/api/v1/learning/plans";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID PLAN_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private LearningPlanService learningPlanService;

  @Test
  void shouldCreatePlan() throws Exception {
    when(learningPlanService.create(eq(USER_ID), any(LearningPlanRequest.class)))
        .thenReturn(samplePlan());

    mockMvc
        .perform(
            post(BASE_PATH)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"System Design Mastery",
                      "description":"Twelve-week plan",
                      "status":"ACTIVE"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(PLAN_ID.toString()))
        .andExpect(jsonPath("$.data.progressPercent").value(0));

    verify(learningPlanService).create(eq(USER_ID), any(LearningPlanRequest.class));
  }

  @Test
  void shouldGetPlan() throws Exception {
    when(learningPlanService.get(USER_ID, PLAN_ID)).thenReturn(samplePlan());

    mockMvc
        .perform(get(BASE_PATH + "/" + PLAN_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("System Design Mastery"));
  }

  @Test
  void shouldListPlans() throws Exception {
    LearningPlanSummaryResponse summary =
        new LearningPlanSummaryResponse(
            PLAN_ID,
            "System Design Mastery",
            "Twelve-week plan",
            LearningPlanStatus.ACTIVE,
            null,
            0,
            0,
            0,
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"));
    when(learningPlanService.list(eq(USER_ID), any(Pageable.class)))
        .thenReturn(new LearningPlanPageResponse(List.of(summary), 0, 20, 1, 1, true, true));

    mockMvc
        .perform(get(BASE_PATH).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(1));
  }

  @Test
  void shouldDeletePlan() throws Exception {
    mockMvc
        .perform(delete(BASE_PATH + "/" + PLAN_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
    verify(learningPlanService).delete(USER_ID, PLAN_ID);
  }

  @Test
  void shouldUpdatePlan() throws Exception {
    when(learningPlanService.update(eq(USER_ID), eq(PLAN_ID), any(LearningPlanRequest.class)))
        .thenReturn(samplePlan());

    mockMvc
        .perform(
            put(BASE_PATH + "/" + PLAN_ID)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"System Design Mastery",
                      "status":"ACTIVE"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  private static LearningPlanResponse samplePlan() {
    return new LearningPlanResponse(
        PLAN_ID,
        "System Design Mastery",
        "Twelve-week plan",
        LearningPlanStatus.ACTIVE,
        null,
        0,
        0,
        0,
        List.of(),
        Instant.parse("2026-08-04T06:00:00Z"),
        Instant.parse("2026-08-04T06:00:00Z"));
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
