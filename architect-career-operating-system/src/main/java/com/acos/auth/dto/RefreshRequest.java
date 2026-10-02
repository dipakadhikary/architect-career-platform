package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Refresh-token request payload.
 *
 * @param refreshToken opaque refresh token
 */
@Schema(name = "RefreshRequest", description = "Refresh token exchange request")
public record RefreshRequest(
    @Schema(
            description = "Opaque refresh token",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotBlank(message = "refreshToken must not be blank") String refreshToken) {}
