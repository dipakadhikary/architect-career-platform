package com.acos.auth.controller;

import com.acos.auth.dto.AuthenticationResponse;
import com.acos.auth.dto.LoginRequest;
import com.acos.auth.dto.LoginResponse;
import com.acos.auth.dto.LogoutRequest;
import com.acos.auth.dto.RefreshRequest;
import com.acos.auth.dto.RegisterRequest;
import com.acos.auth.dto.RegisterResponse;
import com.acos.auth.security.AcosUserDetails;
import com.acos.auth.service.AuthService;
import com.acos.auth.token.TokenResponse;
import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for authentication use-cases. */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration, login, and token APIs")
public class AuthController {

  private final AuthService authService;

  /**
   * Creates the auth controller.
   *
   * @param authService authentication service
   */
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Registers a new platform user.
   *
   * @param request registration payload
   * @return created user details wrapped in {@link ApiResponse}
   */
  @PostMapping(
      path = "/register",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirements
  @Operation(
      summary = "Register a new user",
      description =
          "Creates a new user account with the default USER role. Passwords are validated against"
              + " platform policy and stored using BCrypt.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "User registered successfully",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RegisterApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed or password policy violated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Email address is already registered",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<RegisterResponse>> register(
      @Valid @RequestBody RegisterRequest request) {
    RegisterResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Authenticates an existing platform user and issues tokens.
   *
   * @param request login payload
   * @return authentication response wrapped in {@link ApiResponse}
   */
  @PostMapping(
      path = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirements
  @Operation(
      summary = "Login",
      description =
          "Authenticates a user with email and password and issues a JWT access token plus opaque"
              + " refresh token.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Authentication succeeded",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Request validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid email or password",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "Account is disabled",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    AuthenticationResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Returns the currently authenticated user profile.
   *
   * @param principal authenticated principal
   * @return user profile
   */
  @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirement(name = "bearer-jwt")
  @Operation(summary = "Current user", description = "Returns the authenticated user profile.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Current user profile",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = MeApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<LoginResponse>> getCurrentUser(
      @AuthenticationPrincipal AcosUserDetails principal) {
    LoginResponse response = authService.currentUser(principal.getId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Exchanges a refresh token for a new access/refresh token pair.
   *
   * @param request refresh payload
   * @return new tokens
   */
  @PostMapping(
      path = "/refresh",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirements
  @Operation(
      summary = "Refresh tokens",
      description = "Rotates a valid refresh token into a new access/refresh token pair.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Tokens refreshed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TokenApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid or expired refresh token",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<TokenResponse>> refresh(
      @Valid @RequestBody RefreshRequest request) {
    TokenResponse response = authService.refresh(request.refreshToken());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Revokes the presented refresh token.
   *
   * @param request logout payload
   * @return empty success response
   */
  @PostMapping(
      path = "/logout",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirement(name = "bearer-jwt")
  @Operation(
      summary = "Logout",
      description = "Revokes the presented refresh token. Requires a valid access token.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Refresh token revoked",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VoidApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
    authService.logout(request.refreshToken());
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * OpenAPI schema for a successful registration envelope.
   *
   * @param success whether the request succeeded
   * @param data registered user payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "RegisterApiResponse", description = "Successful registration envelope")
  public record RegisterApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Registered user payload") RegisterResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful authentication envelope.
   *
   * @param success whether the request succeeded
   * @param data authentication payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "AuthenticationApiResponse", description = "Successful authentication envelope")
  public record AuthenticationApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Authentication payload") AuthenticationResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for the current-user envelope.
   *
   * @param success whether the request succeeded
   * @param data user profile payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "MeApiResponse", description = "Current user envelope")
  public record MeApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "User profile payload") LoginResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a token envelope.
   *
   * @param success whether the request succeeded
   * @param data token payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "TokenApiResponse", description = "Token envelope")
  public record TokenApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Token payload") TokenResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for an empty success envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null}
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "VoidApiResponse", description = "Empty success envelope")
  public record VoidApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for an error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on failure
   * @param error error payload
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "ErrorApiResponse", description = "Error response envelope")
  public record ErrorApiResponse(
      @Schema(description = "Whether the request succeeded", example = "false") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload") ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
