package com.acos.career.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.career.dto.RecruiterRequest;
import com.acos.career.dto.RecruiterResponse;
import com.acos.career.entity.RecruiterStatus;
import com.acos.career.service.RecruiterService;
import com.acos.common.handler.GlobalExceptionHandler;
import java.time.Instant;
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

/** MockMvc slice tests for {@link RecruiterController}. */
@WebMvcTest(controllers = RecruiterController.class)
@Import({
  GlobalExceptionHandler.class,
  RecruiterControllerTest.PermitAllSecurityConfiguration.class
})
class RecruiterControllerTest {

  private static final String BASE_PATH = "/api/v1/career/recruiters";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID RECRUITER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final UUID COMPANY_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private RecruiterService recruiterService;

  @Test
  void shouldCreateRecruiter() throws Exception {
    when(recruiterService.create(eq(USER_ID), any(RecruiterRequest.class)))
        .thenReturn(sampleRecruiter());

    mockMvc
        .perform(
            post(BASE_PATH)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "companyId":"%s",
                      "fullName":"Jamie Recruiter",
                      "email":"jamie@agency.example.com"
                    }
                    """
                        .formatted(COMPANY_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(RECRUITER_ID.toString()))
        .andExpect(jsonPath("$.data.fullName").value("Jamie Recruiter"));

    verify(recruiterService).create(eq(USER_ID), any(RecruiterRequest.class));
  }

  @Test
  void shouldGetRecruiter() throws Exception {
    when(recruiterService.get(USER_ID, RECRUITER_ID)).thenReturn(sampleRecruiter());

    mockMvc
        .perform(get(BASE_PATH + "/" + RECRUITER_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.fullName").value("Jamie Recruiter"));
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  private static RecruiterResponse sampleRecruiter() {
    Instant now = Instant.parse("2026-08-04T06:00:00Z");
    return new RecruiterResponse(
        RECRUITER_ID,
        COMPANY_ID,
        "Jamie Recruiter",
        "jamie@agency.example.com",
        null,
        null,
        null,
        null,
        RecruiterStatus.ACTIVE,
        null,
        false,
        null,
        now,
        now,
        0L);
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
