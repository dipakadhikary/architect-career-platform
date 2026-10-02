package com.acos.portfolio.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

/** User-scoped technology used by portfolio projects. */
@Entity
@Table(
    name = "portfolio_technologies",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_portfolio_technologies_owner_name",
            columnNames = {"owner_id", "name"}))
public class Technology extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "category", length = 100)
  private String category;

  /** Creates an empty technology for JPA. */
  protected Technology() {}

  /**
   * Creates a technology for an owner.
   *
   * @param ownerId owning user id
   * @param name technology name
   * @param category optional category
   */
  public Technology(UUID ownerId, String name, String category) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.category = category;
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
   * Returns the technology name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Updates the technology name.
   *
   * @param name new name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the optional category.
   *
   * @return category, may be {@code null}
   */
  public String getCategory() {
    return category;
  }

  /**
   * Updates the optional category.
   *
   * @param category new category
   */
  public void setCategory(String category) {
    this.category = category;
  }
}
