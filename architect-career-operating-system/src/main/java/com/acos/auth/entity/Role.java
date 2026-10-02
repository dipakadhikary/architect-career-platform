package com.acos.auth.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.util.Objects;

/** Authorization role aggregate persisted in the {@code roles} table. */
@Entity
@Table(
    name = "roles",
    schema = "acos",
    uniqueConstraints = @UniqueConstraint(name = "uk_roles_name", columnNames = "name"))
public class Role extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  @Column(name = "name", nullable = false, length = 50)
  private RoleType name;

  /** Creates an empty role for JPA. */
  protected Role() {}

  /**
   * Creates a role with the given type.
   *
   * @param name role type
   */
  public Role(RoleType name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the role type.
   *
   * @return role type
   */
  public RoleType getName() {
    return name;
  }

  /**
   * Updates the role type.
   *
   * @param name role type
   */
  public void setName(RoleType name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }
}
