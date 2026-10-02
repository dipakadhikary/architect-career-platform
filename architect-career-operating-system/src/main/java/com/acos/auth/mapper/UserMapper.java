package com.acos.auth.mapper;

import com.acos.auth.dto.LoginResponse;
import com.acos.auth.dto.RegisterRequest;
import com.acos.auth.dto.RegisterResponse;
import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import com.acos.auth.entity.User;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/** MapStruct mappings between auth domain objects and DTOs. */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

  /**
   * Maps a persisted user to a registration response.
   *
   * @param user persisted user
   * @return registration response
   */
  RegisterResponse toRegisterResponse(User user);

  /**
   * Maps a persisted user to a login response.
   *
   * @param user persisted user
   * @return login response
   */
  @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoleTypes")
  LoginResponse toLoginResponse(User user);

  /**
   * Creates a user aggregate from a registration request and password hash.
   *
   * @param request registration request
   * @param passwordHash bcrypt password hash
   * @return new user entity (not yet persisted)
   */
  default User toUser(RegisterRequest request, String passwordHash) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(passwordHash, "passwordHash must not be null");
    return new User(
        normalizeEmail(request.email()),
        passwordHash,
        request.firstName().trim(),
        request.lastName().trim());
  }

  /**
   * Normalizes an email for storage and uniqueness checks.
   *
   * @param email raw email
   * @return trimmed lower-case email
   */
  @Named("normalizeEmail")
  default String normalizeEmail(String email) {
    Objects.requireNonNull(email, "email must not be null");
    return email.trim().toLowerCase(Locale.ROOT);
  }

  /**
   * Maps role aggregates to role types.
   *
   * @param roles role aggregates
   * @return role types
   */
  @Named("toRoleTypes")
  default Set<RoleType> toRoleTypes(Set<Role> roles) {
    if (roles == null || roles.isEmpty()) {
      return Set.of();
    }
    return Set.copyOf(roles.stream().map(Role::getName).collect(Collectors.toSet()));
  }
}
