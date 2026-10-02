package com.acos.portfolio.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.BatchSize;

/** User-owned portfolio showcase project. */
@Entity
@Table(name = "portfolio_projects", schema = "acos")
public class PortfolioProject extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "summary", nullable = false, length = 500)
  private String summary;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "repository_url", length = 500)
  private String repositoryUrl;

  @Column(name = "live_url", length = 500)
  private String liveUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private ProjectStatus status;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @ManyToMany(fetch = FetchType.LAZY)
  @BatchSize(size = 25)
  @JoinTable(
      name = "portfolio_project_technologies",
      schema = "acos",
      joinColumns =
          @JoinColumn(
              name = "project_id",
              nullable = false,
              foreignKey = @ForeignKey(name = "fk_portfolio_project_technologies_project_id")),
      inverseJoinColumns =
          @JoinColumn(
              name = "technology_id",
              nullable = false,
              foreignKey = @ForeignKey(name = "fk_portfolio_project_technologies_technology_id")))
  private final Set<Technology> technologies = new HashSet<>();

  /** Creates an empty project for JPA. */
  protected PortfolioProject() {}

  /**
   * Creates a portfolio project.
   *
   * @param ownerId owning user id
   * @param title project title
   * @param summary short summary
   * @param description detailed description
   * @param status project status
   */
  public PortfolioProject(
      UUID ownerId, String title, String summary, String description, ProjectStatus status) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.summary = Objects.requireNonNull(summary, "summary must not be null");
    this.description = Objects.requireNonNull(description, "description must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
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
   * Returns the summary.
   *
   * @return summary
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Updates the summary.
   *
   * @param summary new summary
   */
  public void setSummary(String summary) {
    this.summary = Objects.requireNonNull(summary, "summary must not be null");
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
   * Returns the optional repository URL.
   *
   * @return repository URL, may be {@code null}
   */
  public String getRepositoryUrl() {
    return repositoryUrl;
  }

  /**
   * Updates the optional repository URL.
   *
   * @param repositoryUrl new repository URL
   */
  public void setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

  /**
   * Returns the optional live URL.
   *
   * @return live URL, may be {@code null}
   */
  public String getLiveUrl() {
    return liveUrl;
  }

  /**
   * Updates the optional live URL.
   *
   * @param liveUrl new live URL
   */
  public void setLiveUrl(String liveUrl) {
    this.liveUrl = liveUrl;
  }

  /**
   * Returns the project status.
   *
   * @return status
   */
  public ProjectStatus getStatus() {
    return status;
  }

  /**
   * Updates the project status.
   *
   * @param status new status
   */
  public void setStatus(ProjectStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  /**
   * Returns the optional start date.
   *
   * @return start date, may be {@code null}
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Updates the optional start date.
   *
   * @param startDate new start date
   */
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * Returns the optional end date.
   *
   * @return end date, may be {@code null}
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Updates the optional end date.
   *
   * @param endDate new end date
   */
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   * Returns an unmodifiable view of assigned technologies.
   *
   * @return technologies
   */
  public Set<Technology> getTechnologies() {
    return Collections.unmodifiableSet(technologies);
  }

  /**
   * Replaces assigned technologies.
   *
   * @param technologies new technologies
   */
  public void replaceTechnologies(Set<Technology> technologies) {
    Objects.requireNonNull(technologies, "technologies must not be null");
    this.technologies.clear();
    this.technologies.addAll(technologies);
  }
}
