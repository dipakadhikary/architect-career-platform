package com.acos.auth.repository;

import com.acos.auth.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link User}. */
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Finds a user by email address, eagerly loading roles.
   *
   * @param email email address
   * @return matching user, if present
   */
  @EntityGraph(attributePaths = "roles")
  Optional<User> findByEmail(String email);

  /**
   * Finds a user by id, eagerly loading roles.
   *
   * @param id user identifier
   * @return matching user, if present
   */
  @EntityGraph(attributePaths = "roles")
  Optional<User> findDetailedById(UUID id);

  /**
   * Checks whether a user exists for the given email.
   *
   * @param email email address
   * @return {@code true} when a user exists
   */
  boolean existsByEmail(String email);
}
