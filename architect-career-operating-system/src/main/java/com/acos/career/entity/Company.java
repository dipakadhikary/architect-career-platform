package com.acos.career.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** User-owned company tracked for job applications. */
@Entity
@Table(name = "career_companies", schema = "acos")
public class Company extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "name", nullable = false, length = 200)
  private String name;

  @Column(name = "website", length = 500)
  private String website;

  @Column(name = "industry", length = 100)
  private String industry;

  @Column(name = "location", length = 200)
  private String location;

  @Column(name = "notes", length = 2000)
  private String notes;

  @Column(name = "archived", nullable = false)
  private boolean archived;

  @Column(name = "archived_at")
  private Instant archivedAt;

  /** Creates an empty company for JPA. */
  protected Company() {}

  /**
   * Creates a company for an owner.
   *
   * @param ownerId owning user id
   * @param name company name
   */
  public Company(UUID ownerId, String name) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.archived = false;
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
   * Returns the company name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Updates the company name.
   *
   * @param name new name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the optional website.
   *
   * @return website, may be {@code null}
   */
  public String getWebsite() {
    return website;
  }

  /**
   * Updates the optional website.
   *
   * @param website new website
   */
  public void setWebsite(String website) {
    this.website = website;
  }

  /**
   * Returns the optional industry.
   *
   * @return industry, may be {@code null}
   */
  public String getIndustry() {
    return industry;
  }

  /**
   * Updates the optional industry.
   *
   * @param industry new industry
   */
  public void setIndustry(String industry) {
    this.industry = industry;
  }

  /**
   * Returns the optional location.
   *
   * @return location, may be {@code null}
   */
  public String getLocation() {
    return location;
  }

  /**
   * Updates the optional location.
   *
   * @param location new location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns optional notes.
   *
   * @return notes, may be {@code null}
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Updates optional notes.
   *
   * @param notes new notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Reports whether this company has been soft-deleted.
   *
   * @return {@code true} when archived
   */
  public boolean isArchived() {
    return archived;
  }

  /**
   * Returns the archival timestamp.
   *
   * @return archived-at instant, may be {@code null}
   */
  public Instant getArchivedAt() {
    return archivedAt;
  }

  /** Soft-deletes this company. */
  public void archive() {
    this.archived = true;
    this.archivedAt = Instant.now();
  }
}
