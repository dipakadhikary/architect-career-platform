package com.acos.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Repository slice tests for {@link RoleRepository}. */
class RoleRepositoryTest extends RepositoryTestSupport {

  @Autowired private RoleRepository roleRepository;

  @Test
  void shouldFindSeededRolesByName() {
    Optional<Role> userRole = roleRepository.findByName(RoleType.USER);
    Optional<Role> adminRole = roleRepository.findByName(RoleType.ADMIN);

    assertThat(userRole).isPresent();
    assertThat(userRole.get().getId()).isNotNull();
    assertThat(userRole.get().getName()).isEqualTo(RoleType.USER);

    assertThat(adminRole).isPresent();
    assertThat(adminRole.get().getName()).isEqualTo(RoleType.ADMIN);
  }

  @Test
  void shouldReportRoleExistence() {
    assertThat(roleRepository.existsByName(RoleType.USER)).isTrue();
    assertThat(roleRepository.existsByName(RoleType.ADMIN)).isTrue();
  }

  @Test
  void shouldFindRoleById() {
    Role userRole =
        roleRepository
            .findByName(RoleType.USER)
            .orElseThrow(() -> new IllegalStateException("USER role seed missing"));

    Optional<Role> found = roleRepository.findById(userRole.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(RoleType.USER);
  }

  @Test
  void shouldReturnEmptyForUnknownId() {
    assertThat(roleRepository.findById(UUID.randomUUID())).isEmpty();
  }
}
