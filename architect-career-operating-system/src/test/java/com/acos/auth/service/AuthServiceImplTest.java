package com.acos.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.auth.dto.AuthenticationResponse;
import com.acos.auth.dto.LoginRequest;
import com.acos.auth.dto.LoginResponse;
import com.acos.auth.dto.RegisterRequest;
import com.acos.auth.dto.RegisterResponse;
import com.acos.auth.entity.RefreshToken;
import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import com.acos.auth.entity.User;
import com.acos.auth.exception.AccountDisabledException;
import com.acos.auth.exception.EmailAlreadyExistsException;
import com.acos.auth.exception.InvalidCredentialsException;
import com.acos.auth.exception.InvalidTokenException;
import com.acos.auth.exception.RoleNotFoundException;
import com.acos.auth.exception.WeakPasswordException;
import com.acos.auth.mapper.UserMapper;
import com.acos.auth.repository.RoleRepository;
import com.acos.auth.repository.UserRepository;
import com.acos.auth.token.TokenResponse;
import com.acos.auth.token.TokenService;
import com.acos.auth.validator.PasswordValidator;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link AuthServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  private static final String VALID_PASSWORD = "Str0ng!Pass12";
  private static final String EMAIL = "ada@acos.local";
  private static final String FIRST_NAME = "Ada";
  private static final String LAST_NAME = "Lovelace";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private TokenService tokenService;

  private PasswordEncoder passwordEncoder;
  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    passwordEncoder = new BCryptPasswordEncoder();
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    authService =
        new AuthServiceImpl(
            userRepository,
            roleRepository,
            passwordEncoder,
            new PasswordValidator(),
            userMapper,
            tokenService);
  }

  @Test
  void shouldRegisterUserWithHashedPasswordAndDefaultRole() {
    RegisterRequest request =
        new RegisterRequest("Ada@Acos.Local", VALID_PASSWORD, FIRST_NAME, LAST_NAME);
    Role userRole = new Role(RoleType.USER);

    when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
    when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(userRole));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    RegisterResponse response = authService.register(request);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User saved = userCaptor.getValue();

    assertThat(saved.getEmail()).isEqualTo(EMAIL);
    assertThat(saved.getFirstName()).isEqualTo(FIRST_NAME);
    assertThat(saved.getLastName()).isEqualTo(LAST_NAME);
    assertThat(saved.isEnabled()).isTrue();
    assertThat(saved.getRoles()).extracting(Role::getName).containsExactly(RoleType.USER);
    assertThat(passwordEncoder.matches(VALID_PASSWORD, saved.getPasswordHash())).isTrue();

    assertThat(response.email()).isEqualTo(EMAIL);
    assertThat(response.firstName()).isEqualTo(FIRST_NAME);
    assertThat(response.lastName()).isEqualTo(LAST_NAME);
    assertThat(response.enabled()).isTrue();
  }

  @Test
  void shouldRejectDuplicateEmail() {
    RegisterRequest request = new RegisterRequest(EMAIL, VALID_PASSWORD, FIRST_NAME, LAST_NAME);
    when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessageContaining(EMAIL);

    verify(userRepository, never()).save(any());
    verify(roleRepository, never()).findByName(any());
  }

  @Test
  void shouldRejectWeakPassword() {
    RegisterRequest request = new RegisterRequest(EMAIL, "weak", FIRST_NAME, LAST_NAME);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(WeakPasswordException.class);

    verify(userRepository, never()).existsByEmail(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void shouldFailWhenDefaultRoleIsMissing() {
    RegisterRequest request = new RegisterRequest(EMAIL, VALID_PASSWORD, FIRST_NAME, LAST_NAME);
    when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
    when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(RoleNotFoundException.class)
        .hasMessageContaining("USER");

    verify(userRepository, never()).save(any());
  }

  @Test
  void shouldLoginWithValidCredentialsAndIssueTokens() {
    User user = persistedUser(VALID_PASSWORD);
    TokenResponse tokens = TokenResponse.bearer("access-token", "refresh-token", 900L);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(tokenService.issueTokens(user)).thenReturn(tokens);

    AuthenticationResponse response =
        authService.login(new LoginRequest("Ada@Acos.Local", VALID_PASSWORD));

    assertThat(response.user().email()).isEqualTo(EMAIL);
    assertThat(response.user().roles()).containsExactly(RoleType.USER);
    assertThat(response.tokens()).isEqualTo(tokens);
    verify(tokenService).issueTokens(user);
  }

  @Test
  void shouldRejectUnknownEmail() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(new LoginRequest(EMAIL, VALID_PASSWORD)))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessage("Invalid email or password");

    verify(tokenService, never()).issueTokens(any());
  }

  @Test
  void shouldRejectWrongPassword() {
    User user = persistedUser(VALID_PASSWORD);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> authService.login(new LoginRequest(EMAIL, "Wr0ng!Pass")))
        .isInstanceOf(InvalidCredentialsException.class);

    verify(tokenService, never()).issueTokens(any());
  }

  @Test
  void shouldRejectDisabledAccount() {
    User user = persistedUser(VALID_PASSWORD);
    user.setEnabled(false);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> authService.login(new LoginRequest(EMAIL, VALID_PASSWORD)))
        .isInstanceOf(AccountDisabledException.class)
        .hasMessage("Account is disabled");

    verify(tokenService, never()).issueTokens(any());
  }

  @Test
  void shouldReturnCurrentUser() {
    User user = persistedUser(VALID_PASSWORD);
    when(userRepository.findDetailedById(USER_ID)).thenReturn(Optional.of(user));

    LoginResponse response = authService.currentUser(USER_ID);

    assertThat(response.email()).isEqualTo(EMAIL);
    assertThat(response.roles()).containsExactly(RoleType.USER);
  }

  @Test
  void shouldRefreshTokensWithRotation() {
    User user = persistedUser(VALID_PASSWORD);
    RefreshToken refreshToken =
        new RefreshToken(user, "hash", Instant.parse("2026-08-05T06:00:00Z"));
    TokenResponse tokens = TokenResponse.bearer("new-access", "new-refresh", 900L);
    when(tokenService.findActiveRefreshToken("old-refresh")).thenReturn(Optional.of(refreshToken));
    when(tokenService.issueTokens(user)).thenReturn(tokens);

    TokenResponse response = authService.refresh("old-refresh");

    assertThat(response).isEqualTo(tokens);
    verify(tokenService).revokeRefreshToken("old-refresh");
    verify(tokenService).issueTokens(user);
  }

  @Test
  void shouldRejectInvalidRefreshToken() {
    when(tokenService.findActiveRefreshToken("bad")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.refresh("bad")).isInstanceOf(InvalidTokenException.class);
  }

  @Test
  void shouldLogoutByRevokingRefreshToken() {
    authService.logout("refresh-token");

    verify(tokenService).revokeRefreshToken("refresh-token");
  }

  private User persistedUser(String rawPassword) {
    User user = new User(EMAIL, passwordEncoder.encode(rawPassword), FIRST_NAME, LAST_NAME);
    user.addRole(new Role(RoleType.USER));
    ReflectionTestUtils.setField(user, "id", USER_ID);
    return user;
  }
}
