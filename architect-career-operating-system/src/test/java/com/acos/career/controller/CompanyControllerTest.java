package com.acos.career.controller;

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
import com.acos.career.dto.CompanyRequest;
import com.acos.career.dto.CompanyResponse;
import com.acos.career.service.CompanyService;
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

/** MockMvc slice tests for {@link CompanyController}. */
@WebMvcTest(controllers = CompanyController.class)
@Import({GlobalExceptionHandler.class, CompanyControllerTest.PermitAllSecurityConfiguration.class})
class CompanyControllerTest {

  private static final String BASE_PATH = "/api/v1/career/companies";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID COMPANY_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private CompanyService companyService;

  @Test
  void shouldCreateCompany() throws Exception {
    when(companyService.create(eq(USER_ID), any(CompanyRequest.class))).thenReturn(sampleCompany());

    mockMvc
        .perform(
            post(BASE_PATH)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "name":"Acme Corp",
                      "website":"https://acme.example.com",
                      "industry":"Technology"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(COMPANY_ID.toString()))
        .andExpect(jsonPath("$.data.name").value("Acme Corp"));

    verify(companyService).create(eq(USER_ID), any(CompanyRequest.class));
  }

  @Test
  void shouldGetCompany() throws Exception {
    when(companyService.get(USER_ID, COMPANY_ID)).thenReturn(sampleCompany());

    mockMvc
        .perform(get(BASE_PATH + "/" + COMPANY_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("Acme Corp"));
  }

  @Test
  void shouldListCompanies() throws Exception {
    when(companyService.list(USER_ID)).thenReturn(List.of(sampleCompany()));

    mockMvc
        .perform(get(BASE_PATH).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(1));
  }

  @Test
  void shouldUpdateCompany() throws Exception {
    when(companyService.update(eq(USER_ID), eq(COMPANY_ID), any(CompanyRequest.class)))
        .thenReturn(sampleCompany());

    mockMvc
        .perform(
            put(BASE_PATH + "/" + COMPANY_ID)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "name":"Acme Corp",
                      "industry":"Technology"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  void shouldDeleteCompany() throws Exception {
    mockMvc
        .perform(delete(BASE_PATH + "/" + COMPANY_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
    verify(companyService).delete(USER_ID, COMPANY_ID);
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  private static CompanyResponse sampleCompany() {
    return new CompanyResponse(
        COMPANY_ID,
        "Acme Corp",
        "https://acme.example.com",
        "Technology",
        null,
        null,
        false,
        null,
        Instant.parse("2026-08-04T06:00:00Z"),
        Instant.parse("2026-08-04T06:00:00Z"),
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
