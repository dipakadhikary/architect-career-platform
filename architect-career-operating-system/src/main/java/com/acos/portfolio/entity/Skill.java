package com.acos.portfolio.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/** User-owned professional skill. */
@Entity
@Table(
    name = "portfolio_skills",
    schema = "acos",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_portfolio_skills_owner_name",
            columnNames = {"owner_id", "name"}))
public class Skill extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "proficiency_level", nullable = false, length = 32)
  private ProficiencyLevel proficiencyLevel;

  @Column(name = "years_of_experience", precision = 4, scale = 1)
  private BigDecimal yearsOfExperience;

  @Column(name = "description", length = 1000)
  private String description;

  /** Creates an empty skill for JPA. */
  protected Skill() {}

  /**
   * Creates a skill for an owner.
   *
   * @param ownerId owning user id
   * @param name skill name
   * @param proficiencyLevel proficiency level
   * @param yearsOfExperience optional years of experience
   * @param description optional description
   */
  public Skill(
      UUID ownerId,
      String name,
      ProficiencyLevel proficiencyLevel,
      BigDecimal yearsOfExperience,
      String description) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.proficiencyLevel =
        Objects.requireNonNull(proficiencyLevel, "proficiencyLevel must not be null");
    this.yearsOfExperience = yearsOfExperience;
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
   * Returns the skill name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Updates the skill name.
   *
   * @param name new name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  /**
   * Returns the proficiency level.
   *
   * @return proficiency level
   */
  public ProficiencyLevel getProficiencyLevel() {
    return proficiencyLevel;
  }

  /**
   * Updates the proficiency level.
   *
   * @param proficiencyLevel new proficiency level
   */
  public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) {
    this.proficiencyLevel =
        Objects.requireNonNull(proficiencyLevel, "proficiencyLevel must not be null");
  }

  /**
   * Returns optional years of experience.
   *
   * @return years of experience, may be {@code null}
   */
  public BigDecimal getYearsOfExperience() {
    return yearsOfExperience;
  }

  /**
   * Updates optional years of experience.
   *
   * @param yearsOfExperience new years of experience
   */
  public void setYearsOfExperience(BigDecimal yearsOfExperience) {
    this.yearsOfExperience = yearsOfExperience;
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
