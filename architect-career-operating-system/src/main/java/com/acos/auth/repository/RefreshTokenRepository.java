package com.acos.auth.repository;

import com.acos.auth.entity.RefreshToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link RefreshToken}. */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  /**
   * Finds a refresh token by its SHA-256 hash, eagerly loading the owning user and roles.
   *
   * @param tokenHash token hash
   * @return matching token, if present
   */
  @EntityGraph(attributePaths = {"user", "user.roles"})
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  /**
   * Finds non-revoked refresh tokens for a user.
   *
   * @param userId user identifier
   * @return active (non-revoked) refresh tokens
   */
  List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);
}
