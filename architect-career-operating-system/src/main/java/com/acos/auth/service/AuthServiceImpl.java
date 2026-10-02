package com.acos.auth.service;

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
import com.acos.auth.mapper.UserMapper;
import com.acos.auth.repository.RoleRepository;
import com.acos.auth.repository.UserRepository;
import com.acos.auth.token.TokenResponse;
import com.acos.auth.token.TokenService;
import com.acos.auth.validator.PasswordValidator;
import com.acos.common.exception.ResourceNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link AuthService} implementation. */
@Service
public class AuthServiceImpl implements AuthService {

  /**
   * Valid BCrypt hash used when no user is found so password verification timing stays consistent.
   * Hash corresponds to an unused sentinel value, never a real account password.
   */
  static final String DUMMY_PASSWORD_HASH =
      "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final PasswordValidator passwordValidator;
  private final UserMapper userMapper;
  private final TokenService tokenService;

  /**
   * Creates the auth service.
   *
   * @param userRepository user persistence
   * @param roleRepository role persistence
   * @param passwordEncoder password hashing
   * @param passwordValidator password policy checks
   * @param userMapper DTO/entity mapping
   * @param tokenService token issuance and refresh-token lifecycle
   */
  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      PasswordValidator passwordValidator,
      UserMapper userMapper,
      TokenService tokenService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.passwordValidator = passwordValidator;
    this.userMapper = userMapper;
    this.tokenService = tokenService;
  }

  @Override
  @Transactional
  public RegisterResponse register(RegisterRequest request) {
    Objects.requireNonNull(request, "request must not be null");

    passwordValidator.validate(request.password());

    String email = userMapper.normalizeEmail(request.email());
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistsException(email);
    }

    Role userRole =
        roleRepository
            .findByName(RoleType.USER)
            .orElseThrow(() -> new RoleNotFoundException(RoleType.USER));

    String passwordHash = passwordEncoder.encode(request.password());
    User user = userMapper.toUser(request, passwordHash);
    user.setEmail(email);
    user.addRole(userRole);

    User saved = userRepository.save(user);
    return userMapper.toRegisterResponse(saved);
  }

  @Override
  @Transactional
  public AuthenticationResponse login(LoginRequest request) {
    Objects.requireNonNull(request, "request must not be null");

    String email = userMapper.normalizeEmail(request.email());
    Optional<User> found = userRepository.findByEmail(email);
    String passwordHash = found.map(User::getPasswordHash).orElse(DUMMY_PASSWORD_HASH);

    boolean credentialsValid = passwordEncoder.matches(request.password(), passwordHash);
    if (found.isEmpty() || !credentialsValid) {
      throw new InvalidCredentialsException();
    }

    User user = found.get();
    if (!user.isEnabled()) {
      throw new AccountDisabledException();
    }

    TokenResponse tokens = tokenService.issueTokens(user);
    return new AuthenticationResponse(userMapper.toLoginResponse(user), tokens);
  }

  @Override
  @Transactional(readOnly = true)
  public LoginResponse currentUser(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    User user =
        userRepository
            .findDetailedById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    return userMapper.toLoginResponse(user);
  }

  @Override
  @Transactional
  public TokenResponse refresh(String refreshToken) {
    Objects.requireNonNull(refreshToken, "refreshToken must not be null");

    RefreshToken existing =
        tokenService.findActiveRefreshToken(refreshToken).orElseThrow(InvalidTokenException::new);

    User user = existing.getUser();
    if (!user.isEnabled()) {
      throw new AccountDisabledException();
    }

    tokenService.revokeRefreshToken(refreshToken);
    return tokenService.issueTokens(user);
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {
    Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    tokenService.revokeRefreshToken(refreshToken);
  }
}
