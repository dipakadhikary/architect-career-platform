package com.acos.portfolio.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

/** End-to-end integration tests for the authenticated portfolio APIs. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PortfolioIntegrationTest {

  private static final String PROJECTS_PATH = "/api/v1/portfolio/projects";
  private static final String SKILLS_PATH = "/api/v1/portfolio/skills";
  private static final String ACHIEVEMENTS_PATH = "/api/v1/portfolio/achievements";
  private static final String CERTIFICATIONS_PATH = "/api/v1/portfolio/certifications";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldSupportFullPortfolioLifecycle() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("portfolio");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    MvcResult projectResult =
        mockMvc
            .perform(
                post(PROJECTS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title":"ACOS Platform",
                          "summary":"Career operating system",
                          "description":"Spring Boot platform for architects",
                          "status":"PUBLISHED",
                          "technologyNames":["Java","Spring Boot"]
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.title").value("ACOS Platform"))
            .andExpect(jsonPath("$.data.technologies[0]").value("Java"))
            .andExpect(jsonPath("$.data.technologies[1]").value("Spring Boot"))
            .andReturn();

    String projectId =
        objectMapper
            .readTree(projectResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    mockMvc
        .perform(
            get(PROJECTS_PATH + "/search")
                .param("q", "ACOS")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content[0].id").value(projectId));

    MvcResult skillResult =
        mockMvc
            .perform(
                post(SKILLS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "name":"System Design",
                          "proficiencyLevel":"ADVANCED",
                          "yearsOfExperience":5.5,
                          "description":"Distributed systems"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value("System Design"))
            .andReturn();

    String skillId =
        objectMapper
            .readTree(skillResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    mockMvc
        .perform(
            put(SKILLS_PATH + "/" + skillId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "name":"System Design",
                      "proficiencyLevel":"EXPERT",
                      "yearsOfExperience":6.0
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.proficiencyLevel").value("EXPERT"));

    MvcResult achievementResult =
        mockMvc
            .perform(
                post(ACHIEVEMENTS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title":"Led platform modernization",
                          "description":"Migrated monolith to modular services",
                          "achievedOn":"2025-11-15",
                          "organization":"ACME Corp"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.title").value("Led platform modernization"))
            .andReturn();

    String achievementId =
        objectMapper
            .readTree(achievementResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    MvcResult certificationResult =
        mockMvc
            .perform(
                post(CERTIFICATIONS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "name":"AWS Solutions Architect Associate",
                          "issuer":"Amazon Web Services",
                          "credentialId":"AWS-123456",
                          "issuedOn":"2024-05-01",
                          "expiresOn":"2027-05-01"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.issuer").value("Amazon Web Services"))
            .andReturn();

    String certificationId =
        objectMapper
            .readTree(certificationResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    mockMvc
        .perform(get(SKILLS_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(1));

    mockMvc
        .perform(get(ACHIEVEMENTS_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id").value(achievementId));

    mockMvc
        .perform(get(CERTIFICATIONS_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id").value(certificationId));

    mockMvc
        .perform(
            delete(PROJECTS_PATH + "/" + projectId)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    mockMvc
        .perform(
            get(PROJECTS_PATH + "/" + projectId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
  }

  @Test
  void shouldRejectPortfolioApisWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get(PROJECTS_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }
}
