package com.acos.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.dto.LoginResponse;
import com.acos.auth.dto.RegisterRequest;
import com.acos.auth.dto.RegisterResponse;
import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import com.acos.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/** Unit tests for {@link UserMapper}. */
class UserMapperTest {

  private static final String EMAIL = "ada@acos.local";
  private static final String FIRST_NAME = "Ada";
  private static final String LAST_NAME = "Lovelace";
  private static final String PASSWORD_HASH = "hashed-password";

  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    userMapper = Mappers.getMapper(UserMapper.class);
  }

  @Test
  void shouldNormalizeEmail() {
    assertThat(userMapper.normalizeEmail("  Ada@Acos.Local ")).isEqualTo(EMAIL);
  }

  @Test
  void shouldMapRequestToUserWithPasswordHash() {
    RegisterRequest request =
        new RegisterRequest("Ada@Acos.Local", "Str0ng!Pass12", " Ada ", " Lovelace ");

    User user = userMapper.toUser(request, PASSWORD_HASH);

    assertThat(user.getEmail()).isEqualTo(EMAIL);
    assertThat(user.getPasswordHash()).isEqualTo(PASSWORD_HASH);
    assertThat(user.getFirstName()).isEqualTo(FIRST_NAME);
    assertThat(user.getLastName()).isEqualTo(LAST_NAME);
    assertThat(user.isEnabled()).isTrue();
  }

  @Test
  void shouldMapUserToRegisterResponse() {
    User user = new User(EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME);

    RegisterResponse response = userMapper.toRegisterResponse(user);

    assertThat(response.email()).isEqualTo(EMAIL);
    assertThat(response.firstName()).isEqualTo(FIRST_NAME);
    assertThat(response.lastName()).isEqualTo(LAST_NAME);
    assertThat(response.enabled()).isTrue();
    assertThat(response.id()).isNull();
    assertThat(response.createdAt()).isNull();
  }

  @Test
  void shouldMapUserToLoginResponse() {
    User user = new User(EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME);
    user.addRole(new Role(RoleType.USER));
    user.addRole(new Role(RoleType.ADMIN));

    LoginResponse response = userMapper.toLoginResponse(user);

    assertThat(response.email()).isEqualTo(EMAIL);
    assertThat(response.firstName()).isEqualTo(FIRST_NAME);
    assertThat(response.lastName()).isEqualTo(LAST_NAME);
    assertThat(response.enabled()).isTrue();
    assertThat(response.roles()).containsExactlyInAnyOrder(RoleType.USER, RoleType.ADMIN);
  }
}
