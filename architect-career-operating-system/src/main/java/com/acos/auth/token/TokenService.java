package com.acos.auth.token;

import com.acos.auth.entity.RefreshToken;
import com.acos.auth.entity.User;
import java.util.Optional;
import java.util.UUID;

/** Issues and manages access/refresh token pairs. */
public interface TokenService {

  /**
   * Issues a new access token and opaque refresh token for the user.
   *
   * @param user authenticated user
   * @return token response
   */
  TokenResponse issueTokens(User user);

  /**
   * Finds an active (non-revoked, non-expired) refresh token by raw token value.
   *
   * @param rawRefreshToken raw refresh token presented by the client
   * @return active refresh token, if present
   */
  Optional<RefreshToken> findActiveRefreshToken(String rawRefreshToken);

  /**
   * Revokes a refresh token by raw token value when present.
   *
   * @param rawRefreshToken raw refresh token
   */
  void revokeRefreshToken(String rawRefreshToken);

  /**
   * Revokes all non-revoked refresh tokens for a user.
   *
   * @param userId user identifier
   */
  void revokeAllRefreshTokens(UUID userId);
}
