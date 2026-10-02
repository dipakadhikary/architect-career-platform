package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Login payload for authenticating an existing platform user.
 *
 * @param email account email address
 * @param password plain-text password
 */
@Schema(name = "LoginRequest", description = "Payload used to authenticate a user")
public record LoginRequest(
    @Schema(
            description = "Account email address",
            example = "ada@acos.local",
            maxLength = 320,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "email must not be blank") @Email(message = "email must be a valid email address") @Size(max = 320, message = "email must not exceed 320 characters") String email,
    @Schema(
            description = "Plain-text password",
            example = "Str0ng!Pass12",
            maxLength = 72,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "password must not be blank") @Size(max = 72, message = "password must not exceed 72 characters") String password) {}
