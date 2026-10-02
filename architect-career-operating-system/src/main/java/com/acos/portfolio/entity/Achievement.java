package com.acos.portfolio.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** User-owned career achievement. */
@Entity
@Table(name = "portfolio_achievements", schema = "acos")
public class Achievement extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", nullable = false, length = 2000)
  private String description;

  @Column(name = "achieved_on", nullable = false)
  private LocalDate achievedOn;

  @Column(name = "organization", length = 200)
  private String organization;

  /** Creates an empty achievement for JPA. */
  protected Achievement() {}

  /**
   * Creates an achievement for an owner.
   *
   * @param ownerId owning user id
   * @param title achievement title
   * @param description achievement description
   * @param achievedOn date achieved
   * @param organization optional organization
   */
  public Achievement(
      UUID ownerId, String title, String description, LocalDate achievedOn, String organization) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.description = Objects.requireNonNull(description, "description must not be null");
    this.achievedOn = Objects.requireNonNull(achievedOn, "achievedOn must not be null");
    this.organization = organization;
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
   * Returns the title.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the title.
   *
   * @param title new title
   */
  public void setTitle(String title) {
    this.title = Objects.requireNonNull(title, "title must not be null");
  }

  /**
   * Returns the description.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Updates the description.
   *
   * @param description new description
   */
  public void setDescription(String description) {
    this.description = Objects.requireNonNull(description, "description must not be null");
  }

  /**
   * Returns the date achieved.
   *
   * @return achieved on date
   */
  public LocalDate getAchievedOn() {
    return achievedOn;
  }

  /**
   * Updates the date achieved.
   *
   * @param achievedOn new date
   */
  public void setAchievedOn(LocalDate achievedOn) {
    this.achievedOn = Objects.requireNonNull(achievedOn, "achievedOn must not be null");
  }

  /**
   * Returns the optional organization.
   *
   * @return organization, may be {@code null}
   */
  public String getOrganization() {
    return organization;
  }

  /**
   * Updates the optional organization.
   *
   * @param organization new organization
   */
  public void setOrganization(String organization) {
    this.organization = organization;
  }
}
