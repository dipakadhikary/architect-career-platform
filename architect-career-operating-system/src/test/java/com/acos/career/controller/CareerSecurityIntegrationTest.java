package com.acos.career.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

/** Security-focused integration tests for career APIs (auth, isolation, validation). */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CareerSecurityIntegrationTest {

  private static final String COMPANIES_PATH = "/api/v1/career/companies";
  private static final String APPLICATIONS_PATH = "/api/v1/career/applications";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldRejectUnauthorizedCompanyList() throws Exception {
    mockMvc
        .perform(get(COMPANIES_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldRejectInvalidJwtBearer() throws Exception {
    mockMvc
        .perform(get(COMPANIES_PATH).header("Authorization", "Bearer not-a-valid-jwt"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldIsolateCompaniesAndApplicationsBetweenUsers() throws Exception {
    String emailA = AuthApiTestSupport.uniqueEmail("career-sec-a");
    String emailB = AuthApiTestSupport.uniqueEmail("career-sec-b");
    AuthApiTestSupport.register(mockMvc, emailA);
    AuthApiTestSupport.register(mockMvc, emailB);
    String tokenA = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, emailA);
    String tokenB = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, emailB);

    MvcResult companyResult =
        mockMvc
            .perform(
                post(COMPANIES_PATH)
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "name":"Owner A Company"
                        }
                        """))
            .andExpect(status().isCreated())
            .andReturn();
    String companyId = extractField(companyResult, "id");

    mockMvc
        .perform(get(COMPANIES_PATH + "/" + companyId).header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isNotFound());

    MvcResult applicationResult =
        mockMvc
            .perform(
                post(APPLICATIONS_PATH)
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "companyId":"%s",
                          "title":"Staff Architect",
                          "resumeVersion":"v1",
                          "appliedOn":"2026-08-01"
                        }
                        """
                            .formatted(companyId)))
            .andExpect(status().isCreated())
            .andReturn();
    String applicationId = extractField(applicationResult, "id");

    mockMvc
        .perform(
            patch(APPLICATIONS_PATH + "/" + applicationId + "/status")
                .header("Authorization", "Bearer " + tokenB)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "newStatus":"APPLIED"
                    }
                    """))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldRejectBlankCompanyName() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("career-sec-val");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    mockMvc
        .perform(
            post(COMPANIES_PATH)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "name":""
                    }
                    """))
        .andExpect(status().isBadRequest());
  }

  private String extractField(MvcResult result, String fieldName) throws Exception {
    return objectMapper
        .readTree(result.getResponse().getContentAsString())
        .path("data")
        .path(fieldName)
        .asText();
  }
}
