package com.acos.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Mapped superclass providing UUID identity, optimistic locking, and audit timestamps. */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version
  @Column(name = "version", nullable = false)
  private long version;

  /**
   * Returns the persistent identifier.
   *
   * @return entity UUID, or {@code null} before persistence
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the creation timestamp.
   *
   * @return creation instant
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Returns the last update timestamp.
   *
   * @return last update instant
   */
  public Instant getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Returns the optimistic lock version.
   *
   * @return version counter
   */
  public long getVersion() {
    return version;
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof BaseEntity that)) {
      return false;
    }
    Class<?> thisClass = effectiveClass(this);
    Class<?> otherClass = effectiveClass(that);
    if (thisClass != otherClass) {
      return false;
    }
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public final int hashCode() {
    return effectiveClass(this).hashCode();
  }

  private static Class<?> effectiveClass(Object entity) {
    return entity instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
        : entity.getClass();
  }
}
