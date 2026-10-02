package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registration payload for creating a new platform user.
 *
 * @param email unique email address
 * @param password plain-text password (never persisted)
 * @param firstName first name
 * @param lastName last name
 */
@Schema(name = "RegisterRequest", description = "Payload used to register a new user")
public record RegisterRequest(
    @Schema(
            description = "Unique email address",
            example = "ada@acos.local",
            maxLength = 320,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "email must not be blank") @Email(message = "email must be a valid email address") @Size(max = 320, message = "email must not exceed 320 characters") String email,
    @Schema(
            description = "Plain-text password (never stored)",
            example = "Str0ng!Pass12",
            maxLength = 72,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "password must not be blank") @Size(max = 72, message = "password must not exceed 72 characters") String password,
    @Schema(
            description = "First name",
            example = "Ada",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "firstName must not be blank") @Size(max = 100, message = "firstName must not exceed 100 characters") String firstName,
    @Schema(
            description = "Last name",
            example = "Lovelace",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "lastName must not be blank") @Size(max = 100, message = "lastName must not exceed 100 characters") String lastName) {}
