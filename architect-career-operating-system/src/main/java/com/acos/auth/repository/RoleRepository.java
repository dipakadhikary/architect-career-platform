package com.acos.auth.repository;

import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Role}. */
public interface RoleRepository extends JpaRepository<Role, UUID> {

  /**
   * Finds a role by its canonical type.
   *
   * @param name role type
   * @return matching role, if present
   */
  Optional<Role> findByName(RoleType name);

  /**
   * Checks whether a role exists for the given type.
   *
   * @param name role type
   * @return {@code true} when a role exists
   */
  boolean existsByName(RoleType name);
}
