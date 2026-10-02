package com.acos.portfolio.controller;

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
import com.acos.portfolio.dto.PortfolioProjectPageResponse;
import com.acos.portfolio.dto.PortfolioProjectRequest;
import com.acos.portfolio.dto.PortfolioProjectResponse;
import com.acos.portfolio.entity.ProjectStatus;
import com.acos.portfolio.service.PortfolioProjectService;
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

/** MockMvc slice tests for {@link PortfolioProjectController}. */
@WebMvcTest(controllers = PortfolioProjectController.class)
@Import({
  GlobalExceptionHandler.class,
  PortfolioProjectControllerTest.PermitAllSecurityConfiguration.class
})
class PortfolioProjectControllerTest {

  private static final String BASE_PATH = "/api/v1/portfolio/projects";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID PROJECT_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private PortfolioProjectService portfolioProjectService;

  @Test
  void shouldCreateProject() throws Exception {
    when(portfolioProjectService.create(eq(USER_ID), any(PortfolioProjectRequest.class)))
        .thenReturn(sampleProject());

    mockMvc
        .perform(
            post(BASE_PATH)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"ACOS Platform",
                      "summary":"Career OS",
                      "description":"Detailed description",
                      "status":"PUBLISHED",
                      "technologyNames":["Java","Spring Boot"]
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(PROJECT_ID.toString()))
        .andExpect(jsonPath("$.data.technologies[0]").value("Java"));

    verify(portfolioProjectService).create(eq(USER_ID), any(PortfolioProjectRequest.class));
  }

  @Test
  void shouldGetProject() throws Exception {
    when(portfolioProjectService.get(USER_ID, PROJECT_ID)).thenReturn(sampleProject());

    mockMvc
        .perform(get(BASE_PATH + "/" + PROJECT_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("ACOS Platform"));
  }

  @Test
  void shouldListProjects() throws Exception {
    when(portfolioProjectService.list(eq(USER_ID), any(Pageable.class)))
        .thenReturn(
            new PortfolioProjectPageResponse(List.of(sampleProject()), 0, 20, 1, 1, true, true));

    mockMvc
        .perform(get(BASE_PATH).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(1));
  }

  @Test
  void shouldSearchProjects() throws Exception {
    when(portfolioProjectService.search(eq(USER_ID), eq("ACOS"), any(Pageable.class)))
        .thenReturn(
            new PortfolioProjectPageResponse(List.of(sampleProject()), 0, 20, 1, 1, true, true));

    mockMvc
        .perform(get(BASE_PATH + "/search").param("q", "ACOS").with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content[0].title").value("ACOS Platform"));
  }

  @Test
  void shouldUpdateProject() throws Exception {
    when(portfolioProjectService.update(
            eq(USER_ID), eq(PROJECT_ID), any(PortfolioProjectRequest.class)))
        .thenReturn(sampleProject());

    mockMvc
        .perform(
            put(BASE_PATH + "/" + PROJECT_ID)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"ACOS Platform",
                      "summary":"Career OS",
                      "description":"Detailed description",
                      "status":"PUBLISHED"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  void shouldDeleteProject() throws Exception {
    mockMvc
        .perform(delete(BASE_PATH + "/" + PROJECT_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
    verify(portfolioProjectService).delete(USER_ID, PROJECT_ID);
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  private static PortfolioProjectResponse sampleProject() {
    return new PortfolioProjectResponse(
        PROJECT_ID,
        "ACOS Platform",
        "Career OS",
        "Detailed description",
        null,
        null,
        ProjectStatus.PUBLISHED,
        null,
        null,
        List.of("Java", "Spring Boot"),
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
