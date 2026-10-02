package com.acos.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.dto.AuthenticationResponse;
import com.acos.auth.dto.LoginRequest;
import com.acos.auth.dto.LoginResponse;
import com.acos.auth.dto.LogoutRequest;
import com.acos.auth.dto.RefreshRequest;
import com.acos.auth.dto.RegisterRequest;
import com.acos.auth.dto.RegisterResponse;
import com.acos.auth.entity.RoleType;
import com.acos.auth.exception.AccountDisabledException;
import com.acos.auth.exception.EmailAlreadyExistsException;
import com.acos.auth.exception.InvalidCredentialsException;
import com.acos.auth.exception.InvalidTokenException;
import com.acos.auth.exception.WeakPasswordException;
import com.acos.auth.security.AcosUserDetails;
import com.acos.auth.service.AuthService;
import com.acos.auth.token.TokenResponse;
import com.acos.common.api.ApiError;
import com.acos.common.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Set;
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

/** MockMvc slice tests for {@link AuthController}. */
@WebMvcTest(controllers = AuthController.class)
@Import({GlobalExceptionHandler.class, AuthControllerTest.PermitAllSecurityConfiguration.class})
class AuthControllerTest {

  private static final String REGISTER_PATH = "/api/v1/auth/register";
  private static final String LOGIN_PATH = "/api/v1/auth/login";
  private static final String ME_PATH = "/api/v1/auth/me";
  private static final String REFRESH_PATH = "/api/v1/auth/refresh";
  private static final String LOGOUT_PATH = "/api/v1/auth/logout";
  private static final String EMAIL = "ada@acos.local";
  private static final String FIRST_NAME = "Ada";
  private static final String LAST_NAME = "Lovelace";
  private static final String VALID_PASSWORD = "Str0ng!Pass12";
  private static final String JSON_SUCCESS = "$.success";
  private static final String JSON_ERROR_CODE = "$.error.code";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private AuthService authService;

  @Test
  void shouldRegisterUserAndReturnCreated() throws Exception {
    Instant createdAt = Instant.parse("2026-08-04T06:00:00Z");
    RegisterRequest request = new RegisterRequest(EMAIL, VALID_PASSWORD, FIRST_NAME, LAST_NAME);
    RegisterResponse response =
        new RegisterResponse(USER_ID, EMAIL, FIRST_NAME, LAST_NAME, true, createdAt);

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post(REGISTER_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath(JSON_SUCCESS).value(true))
        .andExpect(jsonPath("$.data.id").value(USER_ID.toString()))
        .andExpect(jsonPath("$.data.email").value(EMAIL));

    verify(authService).register(any(RegisterRequest.class));
  }

  @Test
  void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
    String invalidPayload =
        """
        {
          "email": "not-an-email",
          "password": "",
          "firstName": "",
          "lastName": ""
        }
        """;

    mockMvc
        .perform(post(REGISTER_PATH).contentType(APPLICATION_JSON).content(invalidPayload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath(JSON_SUCCESS).value(false))
        .andExpect(jsonPath(JSON_ERROR_CODE).value("VALIDATION_FAILED"));

    verify(authService, never()).register(any());
  }

  @Test
  void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
    RegisterRequest request = new RegisterRequest(EMAIL, VALID_PASSWORD, FIRST_NAME, LAST_NAME);
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(new EmailAlreadyExistsException(EMAIL));

    mockMvc
        .perform(
            post(REGISTER_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath(JSON_ERROR_CODE).value("EMAIL_ALREADY_EXISTS"));
  }

  @Test
  void shouldReturnBadRequestWhenPasswordPolicyFails() throws Exception {
    RegisterRequest request = new RegisterRequest(EMAIL, VALID_PASSWORD, FIRST_NAME, LAST_NAME);
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(
            new WeakPasswordException(
                "Password does not meet policy requirements",
                List.of(
                    ApiError.FieldErrorDetail.ofField(
                        "password", "must contain a special character"))));

    mockMvc
        .perform(
            post(REGISTER_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath(JSON_ERROR_CODE).value("WEAK_PASSWORD"));
  }

  @Test
  void shouldLoginAndReturnTokens() throws Exception {
    LoginRequest request = new LoginRequest(EMAIL, VALID_PASSWORD);
    AuthenticationResponse response =
        new AuthenticationResponse(
            new LoginResponse(USER_ID, EMAIL, FIRST_NAME, LAST_NAME, true, Set.of(RoleType.USER)),
            TokenResponse.bearer("access-token", "refresh-token", 900L));

    when(authService.login(any(LoginRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post(LOGIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath(JSON_SUCCESS).value(true))
        .andExpect(jsonPath("$.data.user.email").value(EMAIL))
        .andExpect(jsonPath("$.data.tokens.accessToken").value("access-token"))
        .andExpect(jsonPath("$.data.tokens.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.data.tokens.tokenType").value("Bearer"));

    verify(authService).login(any(LoginRequest.class));
  }

  @Test
  void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
    when(authService.login(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException());

    mockMvc
        .perform(
            post(LOGIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, VALID_PASSWORD))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath(JSON_ERROR_CODE).value("INVALID_CREDENTIALS"));
  }

  @Test
  void shouldReturnForbiddenWhenAccountIsDisabled() throws Exception {
    when(authService.login(any(LoginRequest.class))).thenThrow(new AccountDisabledException());

    mockMvc
        .perform(
            post(LOGIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, VALID_PASSWORD))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath(JSON_ERROR_CODE).value("ACCOUNT_DISABLED"));
  }

  @Test
  void shouldReturnCurrentUser() throws Exception {
    LoginResponse profile =
        new LoginResponse(USER_ID, EMAIL, FIRST_NAME, LAST_NAME, true, Set.of(RoleType.USER));
    when(authService.currentUser(USER_ID)).thenReturn(profile);

    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));

    mockMvc
        .perform(
            get(ME_PATH)
                .with(
                    authentication(
                        new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value(EMAIL))
        .andExpect(jsonPath("$.data.roles[0]").value("USER"));
  }

  @Test
  void shouldRefreshTokens() throws Exception {
    TokenResponse tokens = TokenResponse.bearer("new-access", "new-refresh", 900L);
    when(authService.refresh("old-refresh")).thenReturn(tokens);

    mockMvc
        .perform(
            post(REFRESH_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshRequest("old-refresh"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("new-access"))
        .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"));
  }

  @Test
  void shouldReturnUnauthorizedForInvalidRefreshToken() throws Exception {
    when(authService.refresh("bad")).thenThrow(new InvalidTokenException());

    mockMvc
        .perform(
            post(REFRESH_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshRequest("bad"))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath(JSON_ERROR_CODE).value("INVALID_TOKEN"));
  }

  @Test
  void shouldLogout() throws Exception {
    mockMvc
        .perform(
            post(LOGOUT_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LogoutRequest("refresh-token"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath(JSON_SUCCESS).value(true));

    verify(authService).logout("refresh-token");
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
