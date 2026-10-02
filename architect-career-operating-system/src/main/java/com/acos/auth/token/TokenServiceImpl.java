package com.acos.auth.token;

import com.acos.auth.entity.RefreshToken;
import com.acos.auth.entity.Role;
import com.acos.auth.entity.User;
import com.acos.auth.repository.RefreshTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link TokenService} implementation. */
@Service
public class TokenServiceImpl implements TokenService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties jwtProperties;
  private final Clock clock;

  /**
   * Creates the token service.
   *
   * @param jwtTokenProvider access-token provider
   * @param refreshTokenRepository refresh-token persistence
   * @param jwtProperties JWT properties
   * @param clock time source
   */
  public TokenServiceImpl(
      JwtTokenProvider jwtTokenProvider,
      RefreshTokenRepository refreshTokenRepository,
      JwtProperties jwtProperties,
      Clock clock) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtProperties = jwtProperties;
    this.clock = clock;
  }

  @Override
  @Transactional
  public TokenResponse issueTokens(User user) {
    Objects.requireNonNull(user, "user must not be null");
    Objects.requireNonNull(user.getId(), "user id must not be null");

    String accessToken =
        jwtTokenProvider.createAccessToken(
            user.getId(),
            user.getEmail(),
            user.getRoles().stream().map(Role::getName).map(Enum::name).toList());

    String rawRefreshToken = UUID.randomUUID().toString();
    Instant expiresAt = Instant.now(clock).plus(jwtProperties.refreshTokenTtl());
    RefreshToken refreshToken = new RefreshToken(user, sha256(rawRefreshToken), expiresAt);
    refreshTokenRepository.save(refreshToken);

    return TokenResponse.bearer(
        accessToken, rawRefreshToken, jwtTokenProvider.getAccessTokenExpiresInSeconds());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<RefreshToken> findActiveRefreshToken(String rawRefreshToken) {
    Objects.requireNonNull(rawRefreshToken, "rawRefreshToken must not be null");
    Instant now = Instant.now(clock);
    return refreshTokenRepository
        .findByTokenHash(sha256(rawRefreshToken))
        .filter(token -> token.isActive(now));
  }

  @Override
  @Transactional
  public void revokeRefreshToken(String rawRefreshToken) {
    Objects.requireNonNull(rawRefreshToken, "rawRefreshToken must not be null");
    refreshTokenRepository
        .findByTokenHash(sha256(rawRefreshToken))
        .filter(token -> !token.isRevoked())
        .ifPresent(
            token -> {
              token.revoke();
              refreshTokenRepository.save(token);
            });
  }

  @Override
  @Transactional
  public void revokeAllRefreshTokens(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    for (RefreshToken token : refreshTokenRepository.findByUserIdAndRevokedFalse(userId)) {
      token.revoke();
    }
  }

  static String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 not available", exception);
    }
  }
}
