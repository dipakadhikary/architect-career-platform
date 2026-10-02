package com.acos.auth.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.auth.entity.RefreshToken;
import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import com.acos.auth.entity.User;
import com.acos.auth.repository.RefreshTokenRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link TokenServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

  private static final Instant FIXED_INSTANT = Instant.parse("2026-08-04T06:00:00Z");
  private static final String SECRET = "unit-test-secret-key-with-32b-minimum!";

  @Mock private RefreshTokenRepository refreshTokenRepository;

  private JwtTokenProvider jwtTokenProvider;
  private TokenServiceImpl tokenService;

  @BeforeEach
  void setUp() {
    JwtProperties jwtProperties =
        new JwtProperties(SECRET, "acos-test", Duration.ofMinutes(15), Duration.ofDays(7));
    Clock clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    jwtTokenProvider = new JwtTokenProvider(jwtProperties, clock);
    tokenService =
        new TokenServiceImpl(jwtTokenProvider, refreshTokenRepository, jwtProperties, clock);
  }

  @Test
  void shouldIssueAccessAndRefreshTokens() {
    User user = persistedUser();
    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    TokenResponse response = tokenService.issueTokens(user);

    assertThat(response.tokenType()).isEqualTo(TokenResponse.BEARER);
    assertThat(response.expiresIn()).isEqualTo(900L);
    assertThat(response.accessToken()).isNotBlank();
    assertThat(response.refreshToken()).isNotBlank();
    assertThat(jwtTokenProvider.isValid(response.accessToken())).isTrue();
    assertThat(jwtTokenProvider.getUserId(response.accessToken())).isEqualTo(user.getId());
    assertThat(jwtTokenProvider.getRoles(response.accessToken())).containsExactly("USER");

    ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokenRepository).save(captor.capture());
    RefreshToken saved = captor.getValue();
    assertThat(saved.getTokenHash()).isEqualTo(TokenServiceImpl.sha256(response.refreshToken()));
    assertThat(saved.getExpiresAt()).isEqualTo(FIXED_INSTANT.plus(Duration.ofDays(7)));
    assertThat(saved.isRevoked()).isFalse();
  }

  @Test
  void shouldFindActiveRefreshToken() {
    User user = persistedUser();
    String rawToken = UUID.randomUUID().toString();
    RefreshToken refreshToken =
        new RefreshToken(user, TokenServiceImpl.sha256(rawToken), FIXED_INSTANT.plusSeconds(60));
    when(refreshTokenRepository.findByTokenHash(TokenServiceImpl.sha256(rawToken)))
        .thenReturn(Optional.of(refreshToken));

    Optional<RefreshToken> found = tokenService.findActiveRefreshToken(rawToken);

    assertThat(found).contains(refreshToken);
  }

  @Test
  void shouldIgnoreExpiredRefreshToken() {
    User user = persistedUser();
    String rawToken = UUID.randomUUID().toString();
    RefreshToken refreshToken =
        new RefreshToken(user, TokenServiceImpl.sha256(rawToken), FIXED_INSTANT.minusSeconds(1));
    when(refreshTokenRepository.findByTokenHash(TokenServiceImpl.sha256(rawToken)))
        .thenReturn(Optional.of(refreshToken));

    assertThat(tokenService.findActiveRefreshToken(rawToken)).isEmpty();
  }

  @Test
  void shouldRevokeRefreshToken() {
    User user = persistedUser();
    String rawToken = UUID.randomUUID().toString();
    RefreshToken refreshToken =
        new RefreshToken(user, TokenServiceImpl.sha256(rawToken), FIXED_INSTANT.plusSeconds(60));
    when(refreshTokenRepository.findByTokenHash(TokenServiceImpl.sha256(rawToken)))
        .thenReturn(Optional.of(refreshToken));

    tokenService.revokeRefreshToken(rawToken);

    assertThat(refreshToken.isRevoked()).isTrue();
    verify(refreshTokenRepository).save(refreshToken);
  }

  @Test
  void shouldRevokeAllRefreshTokensForUser() {
    User user = persistedUser();
    RefreshToken first =
        new RefreshToken(user, TokenServiceImpl.sha256("one"), FIXED_INSTANT.plusSeconds(60));
    RefreshToken second =
        new RefreshToken(user, TokenServiceImpl.sha256("two"), FIXED_INSTANT.plusSeconds(60));
    when(refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId()))
        .thenReturn(List.of(first, second));

    tokenService.revokeAllRefreshTokens(user.getId());

    assertThat(first.isRevoked()).isTrue();
    assertThat(second.isRevoked()).isTrue();
    verify(refreshTokenRepository, never()).save(any());
  }

  @Test
  void shouldRejectIssueWhenUserIdMissing() {
    User user = new User("ada@acos.local", "hash", "Ada", "Lovelace");

    assertThatThrownBy(() -> tokenService.issueTokens(user))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("user id");
  }

  private static User persistedUser() {
    User user = new User("ada@acos.local", "hash", "Ada", "Lovelace");
    user.addRole(new Role(RoleType.USER));
    ReflectionTestUtils.setField(
        user, "id", UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
    return user;
  }
}
