package com.acos.auth.dto;

import com.acos.auth.token.TokenResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Successful authentication payload containing the user profile and issued tokens.
 *
 * @param user authenticated user profile
 * @param tokens access and refresh token pair
 */
@Schema(name = "AuthenticationResponse", description = "Login result with tokens")
public record AuthenticationResponse(
    @Schema(description = "Authenticated user profile", requiredMode = Schema.RequiredMode.REQUIRED)
        LoginResponse user,
    @Schema(description = "Issued tokens", requiredMode = Schema.RequiredMode.REQUIRED)
        TokenResponse tokens) {}
