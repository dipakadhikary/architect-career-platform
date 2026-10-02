package com.acos.testsupport;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Shared auth helpers for Spring Boot integration tests. */
public final class AuthApiTestSupport {

  public static final String VALID_PASSWORD = "Str0ng!Pass12";
  public static final String REGISTER_PATH = "/api/v1/auth/register";
  public static final String LOGIN_PATH = "/api/v1/auth/login";

  private AuthApiTestSupport() {}

  /**
   * Registers a user with a strong password.
   *
   * @param mockMvc mock MVC
   * @param email unique email
   * @throws Exception when registration fails
   */
  public static void register(MockMvc mockMvc, String email) throws Exception {
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
        .andExpect(status().isCreated());
  }

  /**
   * Logs in and returns the access token.
   *
   * @param mockMvc mock MVC
   * @param objectMapper JSON mapper
   * @param email registered email
   * @return access token
   * @throws Exception when login fails
   */
  public static String loginAccessToken(MockMvc mockMvc, ObjectMapper objectMapper, String email)
      throws Exception {
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
            .andReturn();
    JsonNode tokens =
        objectMapper
            .readTree(result.getResponse().getContentAsString())
            .path("data")
            .path("tokens");
    return tokens.get("accessToken").asText();
  }

  /**
   * Builds a unique email address for a test case.
   *
   * @param prefix local-part prefix
   * @return unique email
   */
  public static String uniqueEmail(String prefix) {
    return prefix + "-" + UUID.randomUUID() + "@acos.local";
  }
}
