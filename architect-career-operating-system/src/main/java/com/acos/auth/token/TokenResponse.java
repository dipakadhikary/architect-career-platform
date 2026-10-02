package com.acos.auth.token;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Access and refresh token pair issued by the token infrastructure.
 *
 * @param accessToken signed JWT access token
 * @param refreshToken opaque refresh token
 * @param tokenType token type, typically {@code Bearer}
 * @param expiresIn access token lifetime in seconds
 */
@Schema(name = "TokenResponse", description = "Issued access and refresh tokens")
public record TokenResponse(
    @Schema(description = "Signed JWT access token", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,
    @Schema(description = "Opaque refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken,
    @Schema(
            description = "Token type",
            example = "Bearer",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String tokenType,
    @Schema(
            description = "Access token lifetime in seconds",
            example = "900",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long expiresIn) {

  /** Standard bearer token type. */
  public static final String BEARER = "Bearer";

  /**
   * Creates a bearer token response.
   *
   * @param accessToken access JWT
   * @param refreshToken opaque refresh token
   * @param expiresInSeconds access TTL in seconds
   * @return token response
   */
  public static TokenResponse bearer(
      String accessToken, String refreshToken, long expiresInSeconds) {
    return new TokenResponse(accessToken, refreshToken, BEARER, expiresInSeconds);
  }
}
