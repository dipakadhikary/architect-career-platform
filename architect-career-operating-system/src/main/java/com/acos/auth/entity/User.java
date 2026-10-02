package com.acos.auth.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Platform user aggregate persisted in the {@code users} table. */
@Entity
@Table(
    name = "users",
    schema = "acos",
    uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "email", nullable = false, length = 320)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(name = "enabled", nullable = false)
  private boolean enabled = true;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      schema = "acos",
      joinColumns =
          @JoinColumn(
              name = "user_id",
              nullable = false,
              foreignKey = @ForeignKey(name = "fk_user_roles_user_id")),
      inverseJoinColumns =
          @JoinColumn(
              name = "role_id",
              nullable = false,
              foreignKey = @ForeignKey(name = "fk_user_roles_role_id")))
  private final Set<Role> roles = new HashSet<>();

  /** Creates an empty user for JPA. */
  protected User() {}

  /**
   * Creates a user with required identity attributes.
   *
   * @param email unique email address
   * @param passwordHash hashed password value
   * @param firstName first name
   * @param lastName last name
   */
  public User(String email, String passwordHash, String firstName, String lastName) {
    this.email = Objects.requireNonNull(email, "email must not be null");
    this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
    this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
    this.lastName = Objects.requireNonNull(lastName, "lastName must not be null");
  }

  /**
   * Returns the email address.
   *
   * @return email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Updates the email address.
   *
   * @param email email
   */
  public void setEmail(String email) {
    this.email = Objects.requireNonNull(email, "email must not be null");
  }

  /**
   * Returns the password hash.
   *
   * @return password hash
   */
  public String getPasswordHash() {
    return passwordHash;
  }

  /**
   * Updates the password hash.
   *
   * @param passwordHash password hash
   */
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
  }

  /**
   * Returns the first name.
   *
   * @return first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Updates the first name.
   *
   * @param firstName first name
   */
  public void setFirstName(String firstName) {
    this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
  }

  /**
   * Returns the last name.
   *
   * @return last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Updates the last name.
   *
   * @param lastName last name
   */
  public void setLastName(String lastName) {
    this.lastName = Objects.requireNonNull(lastName, "lastName must not be null");
  }

  /**
   * Returns whether the account is enabled.
   *
   * @return {@code true} when enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Updates the enabled flag.
   *
   * @param enabled enabled flag
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns an unmodifiable view of assigned roles.
   *
   * @return roles
   */
  public Set<Role> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  /**
   * Assigns a role to the user.
   *
   * @param role role to assign
   */
  public void addRole(Role role) {
    roles.add(Objects.requireNonNull(role, "role must not be null"));
  }

  /**
   * Removes a role from the user.
   *
   * @param role role to remove
   */
  public void removeRole(Role role) {
    roles.remove(Objects.requireNonNull(role, "role must not be null"));
  }
}
