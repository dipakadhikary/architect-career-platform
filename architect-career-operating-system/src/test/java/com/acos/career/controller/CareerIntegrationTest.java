package com.acos.career.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

/** End-to-end integration tests for the authenticated career tracker APIs. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CareerIntegrationTest {

  private static final String COMPANIES_PATH = "/api/v1/career/companies";
  private static final String RECRUITERS_PATH = "/api/v1/career/recruiters";
  private static final String APPLICATIONS_PATH = "/api/v1/career/applications";
  private static final String DASHBOARD_PATH = "/api/v1/career/dashboard";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldSupportFullCareerLifecycleAndDashboard() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("career");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    MvcResult companyResult =
        mockMvc
            .perform(
                post(COMPANIES_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "name":"Acme Corp",
                          "website":"https://acme.example.com",
                          "industry":"Technology",
                          "location":"San Francisco, CA"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value("Acme Corp"))
            .andReturn();

    String companyId = extractField(companyResult, "id");

    MvcResult recruiterResult =
        mockMvc
            .perform(
                post(RECRUITERS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "companyId":"%s",
                          "fullName":"Jamie Recruiter",
                          "email":"jamie@agency.example.com"
                        }
                        """
                            .formatted(companyId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.fullName").value("Jamie Recruiter"))
            .andReturn();

    String recruiterId = extractField(recruiterResult, "id");

    MvcResult applicationResult =
        mockMvc
            .perform(
                post(APPLICATIONS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "companyId":"%s",
                          "recruiterId":"%s",
                          "title":"Staff Software Architect",
                          "jobDescription":"Design and lead platform architecture",
                          "source":"LinkedIn",
                          "location":"Remote",
                          "salaryExpectation":220000.00,
                          "currency":"USD",
                          "resumeVersion":"v3-architect",
                          "appliedOn":"2026-08-01"
                        }
                        """
                            .formatted(companyId, recruiterId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.title").value("Staff Software Architect"))
            .andExpect(jsonPath("$.data.status").value("DRAFT"))
            .andExpect(jsonPath("$.data.company.name").value("Acme Corp"))
            .andExpect(jsonPath("$.data.recruiter.fullName").value("Jamie Recruiter"))
            .andReturn();

    String applicationId = extractField(applicationResult, "id");

    mockMvc
        .perform(
            patch(APPLICATIONS_PATH + "/" + applicationId + "/status")
                .header("Authorization", "Bearer " + accessToken)
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

    mockMvc
        .perform(
            patch(APPLICATIONS_PATH + "/" + applicationId + "/status")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "newStatus":"OFFER",
                      "comments":"invalid jump"
                    }
                    """))
        .andExpect(status().isUnprocessableEntity());

    mockMvc
        .perform(
            get(APPLICATIONS_PATH + "/" + applicationId + "/history")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].newStatus").value("DRAFT"))
        .andExpect(jsonPath("$.data[1].newStatus").value("APPLIED"));

    mockMvc
        .perform(
            get(APPLICATIONS_PATH + "/" + applicationId + "/timeline")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.steps[1].status").value("APPLIED"))
        .andExpect(jsonPath("$.data.steps[1].reached").value(true))
        .andExpect(jsonPath("$.data.steps[1].current").value(true));

    mockMvc
        .perform(
            get(APPLICATIONS_PATH + "/search")
                .header("Authorization", "Bearer " + accessToken)
                .param("keyword", "architect"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].id").value(applicationId));

    mockMvc
        .perform(
            post(APPLICATIONS_PATH + "/" + applicationId + "/interviews")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "interviewRound":"TECHNICAL",
                      "interviewDate":"2099-08-20T15:00:00Z",
                      "status":"SCHEDULED",
                      "locationOrLink":"https://meet.example.com/abc",
                      "interviewer":"Alex Manager"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.interviewRound").value("TECHNICAL"))
        .andExpect(jsonPath("$.data.status").value("SCHEDULED"));

    mockMvc
        .perform(
            post(APPLICATIONS_PATH + "/" + applicationId + "/offers")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "baseSalary":230000.00,
                      "currency":"USD",
                      "stockOptions":"15,000 RSUs over 4 years",
                      "joiningBonus":10000.00,
                      "annualBonus":20000.00,
                      "offerExpiryDate":"2026-09-15",
                      "offerStatus":"PENDING"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.offerStatus").value("PENDING"));

    mockMvc
        .perform(get(DASHBOARD_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalCompanies").value(1))
        .andExpect(jsonPath("$.data.totalRecruiters").value(1))
        .andExpect(jsonPath("$.data.totalActiveApplications").value(1))
        .andExpect(jsonPath("$.data.upcomingInterviews").value(1))
        .andExpect(jsonPath("$.data.pendingOffers").value(1));

    mockMvc
        .perform(
            delete(APPLICATIONS_PATH + "/" + applicationId)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk());

    mockMvc
        .perform(get(APPLICATIONS_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content.length()").value(0));

    mockMvc
        .perform(
            get(APPLICATIONS_PATH + "/archived").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].archived").value(true));
  }

  @Test
  void shouldRejectCareerApisWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get(COMPANIES_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));

    mockMvc
        .perform(get(DASHBOARD_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private String extractField(MvcResult result, String fieldName) throws Exception {
    return objectMapper
        .readTree(result.getResponse().getContentAsString())
        .path("data")
        .path(fieldName)
        .asText();
  }
}
