package com.acos.knowledge.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

/** User-scoped knowledge category. */
@Entity
@Table(
    name = "categories",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_categories_owner_name",
            columnNames = {"owner_id", "name"}))
public class Category extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  /** Creates an empty category for JPA. */
  protected Category() {}

  /**
   * Creates a category for an owner.
   *
   * @param ownerId owning user id
   * @param name category name
   * @param description optional description
   */
  public Category(UUID ownerId, String name, String description) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.description = description;
  }

  /**
   * Returns the owning user id.
   *
   * @return owner id
   */
  public UUID getOwnerId() {
    return ownerId;
  }

  /**
   * Returns the category name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Updates the category name.
   *
   * @param name new name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the optional description.
   *
   * @return description, may be {@code null}
   */
  public String getDescription() {
    return description;
  }

  /**
   * Updates the optional description.
   *
   * @param description new description
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
