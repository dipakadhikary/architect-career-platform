package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Logout request payload.
 *
 * @param refreshToken opaque refresh token to revoke
 */
@Schema(name = "LogoutRequest", description = "Logout request that revokes a refresh token")
public record LogoutRequest(
    @Schema(
            description = "Opaque refresh token to revoke",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotBlank(message = "refreshToken must not be blank") String refreshToken) {}
