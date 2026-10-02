package com.acos.career.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** User-owned job application with soft-archive support. */
@Entity
@Table(name = "career_job_applications", schema = "acos")
public class JobApplication extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "company_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_career_job_applications_company_id"))
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "recruiter_id",
      foreignKey = @ForeignKey(name = "fk_career_job_applications_recruiter_id"))
  private Recruiter recruiter;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "job_description", length = 10_000)
  private String jobDescription;

  @Column(name = "source", length = 100)
  private String source;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 40)
  private ApplicationStatus status;

  @Column(name = "salary_expectation", precision = 12, scale = 2)
  private BigDecimal salaryExpectation;

  @Column(name = "currency", length = 3)
  private String currency;

  @Column(name = "resume_version", length = 100)
  private String resumeVersion;

  @Column(name = "applied_on", nullable = false)
  private LocalDate appliedOn;

  @Column(name = "location", length = 200)
  private String location;

  @Column(name = "job_url", length = 500)
  private String jobUrl;

  @Column(name = "notes", length = 2000)
  private String notes;

  @Column(name = "archived", nullable = false)
  private boolean archived;

  @Column(name = "archived_at")
  private Instant archivedAt;

  @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final List<Interview> interviews = List.of();

  /** Creates an empty job application for JPA. */
  protected JobApplication() {}

  /**
   * Creates a job application, always starting in {@link ApplicationStatus#DRAFT}.
   *
   * @param ownerId owning user id
   * @param company target company
   * @param title job title
   * @param appliedOn application date
   */
  public JobApplication(UUID ownerId, Company company, String title, LocalDate appliedOn) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.company = Objects.requireNonNull(company, "company must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.appliedOn = Objects.requireNonNull(appliedOn, "appliedOn must not be null");
    this.status = ApplicationStatus.DRAFT;
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
   * Returns the target company.
   *
   * @return company
   */
  public Company getCompany() {
    return company;
  }

  /**
   * Updates the target company.
   *
   * @param company company
   */
  public void setCompany(Company company) {
    this.company = Objects.requireNonNull(company, "company must not be null");
  }

  /**
   * Returns the optional recruiter.
   *
   * @return recruiter, may be {@code null}
   */
  public Recruiter getRecruiter() {
    return recruiter;
  }

  /**
   * Updates the optional recruiter.
   *
   * @param recruiter recruiter, may be {@code null}
   */
  public void setRecruiter(Recruiter recruiter) {
    this.recruiter = recruiter;
  }

  /**
   * Returns the job title.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the job title.
   *
   * @param title new title
   */
  public void setTitle(String title) {
    this.title = Objects.requireNonNull(title, "title must not be null");
  }

  /**
   * Returns the optional job description.
   *
   * @return job description, may be {@code null}
   */
  public String getJobDescription() {
    return jobDescription;
  }

  /**
   * Updates the optional job description.
   *
   * @param jobDescription new job description
   */
  public void setJobDescription(String jobDescription) {
    this.jobDescription = jobDescription;
  }

  /**
   * Returns the application status.
   *
   * @return status
   */
  public ApplicationStatus getStatus() {
    return status;
  }

  /**
   * Updates the application status. Intended to be invoked only after validating the transition
   * through the application state machine.
   *
   * @param status new status
   */
  public void setStatus(ApplicationStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  /**
   * Returns the optional source.
   *
   * @return source, may be {@code null}
   */
  public String getSource() {
    return source;
  }

  /**
   * Updates the optional source.
   *
   * @param source new source
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * Returns the optional job URL.
   *
   * @return job URL, may be {@code null}
   */
  public String getJobUrl() {
    return jobUrl;
  }

  /**
   * Updates the optional job URL.
   *
   * @param jobUrl new job URL
   */
  public void setJobUrl(String jobUrl) {
    this.jobUrl = jobUrl;
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
   * Returns the optional salary expectation.
   *
   * @return salary expectation, may be {@code null}
   */
  public BigDecimal getSalaryExpectation() {
    return salaryExpectation;
  }

  /**
   * Updates the optional salary expectation.
   *
   * @param salaryExpectation new salary expectation
   */
  public void setSalaryExpectation(BigDecimal salaryExpectation) {
    this.salaryExpectation = salaryExpectation;
  }

  /**
   * Returns the optional currency code.
   *
   * @return currency, may be {@code null}
   */
  public String getCurrency() {
    return currency;
  }

  /**
   * Updates the optional currency code.
   *
   * @param currency new currency
   */
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  /**
   * Returns the optional resume version identifier.
   *
   * @return resume version, may be {@code null}
   */
  public String getResumeVersion() {
    return resumeVersion;
  }

  /**
   * Updates the optional resume version identifier.
   *
   * @param resumeVersion new resume version
   */
  public void setResumeVersion(String resumeVersion) {
    this.resumeVersion = resumeVersion;
  }

  /**
   * Returns the application date.
   *
   * @return applied-on date
   */
  public LocalDate getAppliedOn() {
    return appliedOn;
  }

  /**
   * Updates the application date.
   *
   * @param appliedOn new applied-on date
   */
  public void setAppliedOn(LocalDate appliedOn) {
    this.appliedOn = Objects.requireNonNull(appliedOn, "appliedOn must not be null");
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
   * Reports whether this application has been archived.
   *
   * @return {@code true} when archived
   */
  public boolean isArchived() {
    return archived;
  }

  /**
   * Returns the optional archival timestamp.
   *
   * @return archived-at instant, may be {@code null}
   */
  public Instant getArchivedAt() {
    return archivedAt;
  }

  /** Soft-archives this application, recording the archival timestamp. */
  public void archive() {
    this.archived = true;
    this.archivedAt = Instant.now();
  }

  /**
   * Returns the interviews scheduled for this application.
   *
   * @return interviews, may be empty
   */
  public List<Interview> getInterviews() {
    return interviews;
  }
}
