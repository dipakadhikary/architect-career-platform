package com.acos.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.testsupport.PostgresTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
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

/** End-to-end security integration tests for JWT authentication flows. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityIntegrationTest {

  private static final String VALID_PASSWORD = "Str0ng!Pass12";
  private static final String REGISTER_PATH = "/api/v1/auth/register";
  private static final String LOGIN_PATH = "/api/v1/auth/login";
  private static final String ME_PATH = "/api/v1/auth/me";
  private static final String REFRESH_PATH = "/api/v1/auth/refresh";
  private static final String LOGOUT_PATH = "/api/v1/auth/logout";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldAuthenticateAndAccessProtectedMeEndpoint() throws Exception {
    String email = uniqueEmail("me");
    register(email);
    JsonNode tokens = login(email);

    mockMvc
        .perform(
            get(ME_PATH).header("Authorization", "Bearer " + tokens.get("accessToken").asText()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.email").value(email))
        .andExpect(jsonPath("$.data.roles[0]").value("USER"));
  }

  @Test
  void shouldRejectMeWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get(ME_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldRefreshTokensAndInvalidateOldRefreshToken() throws Exception {
    String email = uniqueEmail("refresh");
    register(email);
    JsonNode original = login(email);

    MvcResult refreshResult =
        mockMvc
            .perform(
                post(REFRESH_PATH)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {"refreshToken":"%s"}
                        """
                            .formatted(original.get("refreshToken").asText())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
            .andReturn();

    JsonNode refreshed = readData(refreshResult);
    assertThat(refreshed.get("accessToken").asText())
        .isNotEqualTo(original.get("accessToken").asText());
    assertThat(refreshed.get("refreshToken").asText())
        .isNotEqualTo(original.get("refreshToken").asText());

    mockMvc
        .perform(
            get(ME_PATH).header("Authorization", "Bearer " + refreshed.get("accessToken").asText()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value(email));

    mockMvc
        .perform(
            post(REFRESH_PATH)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"refreshToken":"%s"}
                    """
                        .formatted(original.get("refreshToken").asText())))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }

  @Test
  void shouldLogoutAndRejectSubsequentRefresh() throws Exception {
    String email = uniqueEmail("logout");
    register(email);
    JsonNode tokens = login(email);

    mockMvc
        .perform(
            post(LOGOUT_PATH)
                .header("Authorization", "Bearer " + tokens.get("accessToken").asText())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"refreshToken":"%s"}
                    """
                        .formatted(tokens.get("refreshToken").asText())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    mockMvc
        .perform(
            post(REFRESH_PATH)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"refreshToken":"%s"}
                    """
                        .formatted(tokens.get("refreshToken").asText())))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }

  @Test
  void shouldRejectLogoutWithoutAccessToken() throws Exception {
    mockMvc
        .perform(
            post(LOGOUT_PATH)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"refreshToken":"any-token"}
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private void register(String email) throws Exception {
    mockMvc
        .perform(
            post(REGISTER_PATH)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "email":"%s",
                      "password":"%s",
                      "firstName":"Ada",
                      "lastName":"Lovelace"
                    }
                    """
                        .formatted(email, VALID_PASSWORD)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.email").value(email));
  }

  private JsonNode login(String email) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post(LOGIN_PATH)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {"email":"%s","password":"%s"}
                        """
                            .formatted(email, VALID_PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.tokens.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.tokens.refreshToken").isNotEmpty())
            .andReturn();
    return objectMapper
        .readTree(result.getResponse().getContentAsString())
        .path("data")
        .path("tokens");
  }

  private JsonNode readData(MvcResult result) throws Exception {
    return objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
  }

  private static String uniqueEmail(String prefix) {
    return prefix + "-" + UUID.randomUUID() + "@acos.local";
  }
}
