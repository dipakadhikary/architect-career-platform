package com.acos.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.Role;
import com.acos.auth.entity.RoleType;
import com.acos.auth.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Repository slice tests for {@link UserRepository}. */
class UserRepositoryTest extends RepositoryTestSupport {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Test
  void shouldSaveAndFindUserByEmail() {
    Role userRole =
        roleRepository
            .findByName(RoleType.USER)
            .orElseThrow(() -> new IllegalStateException("USER role seed missing"));

    User user =
        new User(
            "architect@acos.local",
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
            "Ada",
            "Lovelace");
    user.addRole(userRole);

    User saved = userRepository.saveAndFlush(user);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();

    Optional<User> found = userRepository.findByEmail("architect@acos.local");

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(saved.getId());
    assertThat(found.get().getFirstName()).isEqualTo("Ada");
    assertThat(found.get().getRoles()).extracting(Role::getName).containsExactly(RoleType.USER);
  }

  @Test
  void shouldReportEmailExistence() {
    userRepository.saveAndFlush(
        new User(
            "exists@acos.local",
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
            "Grace",
            "Hopper"));

    assertThat(userRepository.existsByEmail("exists@acos.local")).isTrue();
    assertThat(userRepository.existsByEmail("missing@acos.local")).isFalse();
  }

  @Test
  void shouldReturnEmptyWhenEmailIsUnknown() {
    Optional<User> found = userRepository.findByEmail("unknown@acos.local");

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindById() {
    User saved =
        userRepository.saveAndFlush(
            new User(
                "id-lookup@acos.local",
                "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
                "Alan",
                "Turing"));

    Optional<User> found = userRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("id-lookup@acos.local");
  }

  @Test
  void shouldFindDetailedByIdWithRoles() {
    Role userRole =
        roleRepository
            .findByName(RoleType.USER)
            .orElseThrow(() -> new IllegalStateException("USER role seed missing"));

    User user =
        new User(
            "detailed@acos.local",
            "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
            "Grace",
            "Hopper");
    user.addRole(userRole);
    User saved = userRepository.saveAndFlush(user);

    Optional<User> found = userRepository.findDetailedById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getRoles()).extracting(Role::getName).containsExactly(RoleType.USER);
  }

  @Test
  void shouldReturnEmptyForUnknownId() {
    assertThat(userRepository.findById(UUID.randomUUID())).isEmpty();
  }
}
