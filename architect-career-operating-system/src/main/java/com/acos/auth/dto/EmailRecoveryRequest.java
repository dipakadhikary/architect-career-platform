package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Email submitted to a public recovery endpoint.
 *
 * @param email address to look up
 */
@Schema(name = "EmailRecoveryRequest", description = "Account recovery email")
public record EmailRecoveryRequest(
    @Schema(description = "Account email address", example = "user@example.com")
        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be a valid email address")
        @Size(max = 320, message = "email must not exceed 320 characters")
        String email) {}
