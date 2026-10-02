package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * Registration result returned after a user account is created.
 *
 * @param id user identifier
 * @param email registered email address
 * @param firstName first name
 * @param lastName last name
 * @param enabled whether the account is enabled
 * @param createdAt account creation timestamp
 */
@Schema(name = "RegisterResponse", description = "Registered user details")
public record RegisterResponse(
    @Schema(
            description = "User identifier",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Registered email address",
            example = "ada@acos.local",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
    @Schema(
            description = "First name",
            example = "Ada",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
    @Schema(
            description = "Last name",
            example = "Lovelace",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
    @Schema(
            description = "Whether the account is enabled",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean enabled,
    @Schema(
            description = "Account creation timestamp",
            example = "2026-08-04T06:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt) {}
