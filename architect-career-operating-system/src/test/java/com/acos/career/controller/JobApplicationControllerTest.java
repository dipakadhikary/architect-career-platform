package com.acos.career.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.CompanySummaryResponse;
import com.acos.career.dto.JobApplicationPageResponse;
import com.acos.career.dto.JobApplicationRequest;
import com.acos.career.dto.JobApplicationResponse;
import com.acos.career.dto.JobApplicationSearchCriteria;
import com.acos.career.dto.StatusTransitionRequest;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.service.JobApplicationService;
import com.acos.common.handler.GlobalExceptionHandler;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

/** MockMvc slice tests for {@link JobApplicationController}. */
@WebMvcTest(controllers = JobApplicationController.class)
@Import({
  GlobalExceptionHandler.class,
  JobApplicationControllerTest.PermitAllSecurityConfiguration.class
})
class JobApplicationControllerTest {

  private static final String BASE_PATH = "/api/v1/career/applications";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID APPLICATION_ID =
      UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final UUID COMPANY_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private JobApplicationService jobApplicationService;

  @Test
  void shouldCreateApplication() throws Exception {
    when(jobApplicationService.create(eq(USER_ID), any(JobApplicationRequest.class)))
        .thenReturn(sampleApplication(ApplicationStatus.DRAFT));

    mockMvc
        .perform(
            post(BASE_PATH)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "companyId":"%s",
                      "title":"Staff Software Architect",
                      "appliedOn":"2026-08-01"
                    }
                    """
                        .formatted(COMPANY_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(APPLICATION_ID.toString()))
        .andExpect(jsonPath("$.data.status").value("DRAFT"));

    verify(jobApplicationService).create(eq(USER_ID), any(JobApplicationRequest.class));
  }

  @Test
  void shouldGetApplication() throws Exception {
    when(jobApplicationService.get(USER_ID, APPLICATION_ID))
        .thenReturn(sampleApplication(ApplicationStatus.DRAFT));

    mockMvc
        .perform(get(BASE_PATH + "/" + APPLICATION_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("Staff Software Architect"));
  }

  @Test
  void shouldTransitionStatus() throws Exception {
    when(jobApplicationService.transitionStatus(
            eq(USER_ID), eq(APPLICATION_ID), any(StatusTransitionRequest.class)))
        .thenReturn(sampleApplication(ApplicationStatus.APPLIED));

    mockMvc
        .perform(
            patch(BASE_PATH + "/" + APPLICATION_ID + "/status")
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "newStatus":"APPLIED",
                      "comments":"Submitted via referral"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("APPLIED"));

    verify(jobApplicationService)
        .transitionStatus(eq(USER_ID), eq(APPLICATION_ID), any(StatusTransitionRequest.class));
  }

  @Test
  void shouldSearchApplications() throws Exception {
    when(jobApplicationService.search(
            eq(USER_ID), any(JobApplicationSearchCriteria.class), any(Pageable.class)))
        .thenReturn(samplePage());

    mockMvc
        .perform(get(BASE_PATH + "/search").with(authenticated()).param("keyword", "architect"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].id").value(APPLICATION_ID.toString()));
  }

  @Test
  void shouldArchiveApplication() throws Exception {
    mockMvc
        .perform(delete(BASE_PATH + "/" + APPLICATION_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
    verify(jobApplicationService).archive(USER_ID, APPLICATION_ID);
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  private static JobApplicationResponse sampleApplication(ApplicationStatus status) {
    Instant now = Instant.parse("2026-08-04T06:00:00Z");
    return new JobApplicationResponse(
        APPLICATION_ID,
        new CompanySummaryResponse(COMPANY_ID, "Acme Corp"),
        null,
        "Staff Software Architect",
        "Design and lead platform architecture",
        "LinkedIn",
        status,
        new BigDecimal("220000.00"),
        "USD",
        "v3-architect",
        LocalDate.parse("2026-08-01"),
        "Remote",
        null,
        null,
        false,
        null,
        now,
        now,
        0L);
  }

  private static JobApplicationPageResponse samplePage() {
    return new JobApplicationPageResponse(
        List.of(sampleApplication(ApplicationStatus.DRAFT)), 0, 20, 1L, 1, true, true);
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
