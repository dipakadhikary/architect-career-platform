package com.acos.auth.repository;

import com.acos.auth.entity.PasswordResetToken;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link PasswordResetToken}. */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

  /**
   * Loads a token by hash and locks the row so two resets cannot both consume it.
   *
   * @param tokenHash SHA-256 hex digest
   * @return matching token, if present
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      select token from PasswordResetToken token
      join fetch token.user
      where token.tokenHash = :tokenHash
      """)
  Optional<PasswordResetToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

  /**
   * Loads unused tokens for a user so a new request can supersede them.
   *
   * @param userId user identifier
   * @return unused tokens
   */
  @Query(
      """
      select token from PasswordResetToken token
      where token.user.id = :userId and token.usedAt is null
      """)
  List<PasswordResetToken> findUnusedByUserId(@Param("userId") UUID userId);

  /**
   * Deletes tokens that expired before the given instant and were never used.
   *
   * @param cutoff expiration cutoff
   * @return number of rows removed
   */
  long deleteByUsedAtIsNullAndExpiresAtBefore(Instant cutoff);
}
