package com.acos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Password reset submission.
 *
 * @param token raw reset token from the email link
 * @param newPassword replacement password
 * @param confirmPassword confirmation of the replacement password
 */
@Schema(name = "ResetPasswordRequest", description = "Reset a password with a single-use token")
public record ResetPasswordRequest(
    @Schema(description = "Raw password reset token")
        @NotBlank(message = "token must not be blank")
        @Size(max = 512, message = "token must not exceed 512 characters")
        String token,
    @Schema(description = "New password")
        @NotBlank(message = "newPassword must not be blank")
        @Size(max = 72, message = "newPassword must not exceed 72 characters")
        String newPassword,
    @Schema(description = "Confirmation of the new password")
        @NotBlank(message = "confirmPassword must not be blank")
        @Size(max = 72, message = "confirmPassword must not exceed 72 characters")
        String confirmPassword) {}
