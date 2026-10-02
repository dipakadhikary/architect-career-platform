package com.acos.auth.dto;

import com.acos.auth.entity.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Login result returned after successful authentication.
 *
 * @param id user identifier
 * @param email authenticated email address
 * @param firstName first name
 * @param lastName last name
 * @param enabled whether the account is enabled
 * @param roles assigned role types
 */
@Schema(name = "LoginResponse", description = "Authenticated user details")
public record LoginResponse(
    @Schema(
            description = "User identifier",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Authenticated email address",
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
    @Schema(description = "Assigned role types", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<RoleType> roles) {

  /**
   * Creates an immutable login response.
   *
   * @param id user identifier
   * @param email authenticated email
   * @param firstName first name
   * @param lastName last name
   * @param enabled enabled flag
   * @param roles assigned roles
   */
  public LoginResponse {
    Objects.requireNonNull(email, "email must not be null");
    Objects.requireNonNull(firstName, "firstName must not be null");
    Objects.requireNonNull(lastName, "lastName must not be null");
    Objects.requireNonNull(roles, "roles must not be null");
  }
}
