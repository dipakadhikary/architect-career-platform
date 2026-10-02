package com.acos.knowledge.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

/** User-scoped knowledge tag. */
@Entity
@Table(
    name = "tags",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_tags_owner_name",
            columnNames = {"owner_id", "name"}))
public class Tag extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  /** Creates an empty tag for JPA. */
  protected Tag() {}

  /**
   * Creates a tag for an owner.
   *
   * @param ownerId owning user id
   * @param name normalized tag name
   */
  public Tag(UUID ownerId, String name) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
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
   * Returns the tag name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }
}
