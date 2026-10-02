package com.acos.career.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** User-owned recruiter contact. */
@Entity
@Table(name = "career_recruiters", schema = "acos")
public class Recruiter extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_id",
      foreignKey = @ForeignKey(name = "fk_career_recruiters_company_id"))
  private Company company;

  @Column(name = "full_name", nullable = false, length = 200)
  private String fullName;

  @Column(name = "email", length = 320)
  private String email;

  @Column(name = "phone", length = 50)
  private String phone;

  @Column(name = "linkedin_url", length = 500)
  private String linkedInUrl;

  @Column(name = "last_contact_date")
  private LocalDate lastContactDate;

  @Column(name = "next_follow_up_date")
  private LocalDate nextFollowUpDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private RecruiterStatus status;

  @Column(name = "notes", length = 2000)
  private String notes;

  @Column(name = "archived", nullable = false)
  private boolean archived;

  @Column(name = "archived_at")
  private Instant archivedAt;

  /** Creates an empty recruiter for JPA. */
  protected Recruiter() {}

  /**
   * Creates a recruiter for an owner, defaulting to {@link RecruiterStatus#ACTIVE}.
   *
   * @param ownerId owning user id
   * @param fullName recruiter full name
   */
  public Recruiter(UUID ownerId, String fullName) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
    this.status = RecruiterStatus.ACTIVE;
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
   * Returns the optional associated company.
   *
   * @return company, may be {@code null}
   */
  public Company getCompany() {
    return company;
  }

  /**
   * Updates the optional associated company.
   *
   * @param company company, may be {@code null}
   */
  public void setCompany(Company company) {
    this.company = company;
  }

  /**
   * Returns the recruiter full name.
   *
   * @return full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Updates the recruiter full name.
   *
   * @param fullName new full name
   */
  public void setFullName(String fullName) {
    this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
  }

  /**
   * Returns the optional email.
   *
   * @return email, may be {@code null}
   */
  public String getEmail() {
    return email;
  }

  /**
   * Updates the optional email.
   *
   * @param email new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the optional phone.
   *
   * @return phone, may be {@code null}
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Updates the optional phone.
   *
   * @param phone new phone
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Returns the optional LinkedIn URL.
   *
   * @return LinkedIn URL, may be {@code null}
   */
  public String getLinkedInUrl() {
    return linkedInUrl;
  }

  /**
   * Updates the optional LinkedIn URL.
   *
   * @param linkedInUrl new LinkedIn URL
   */
  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }

  /**
   * Returns the optional last contact date.
   *
   * @return last contact date, may be {@code null}
   */
  public LocalDate getLastContactDate() {
    return lastContactDate;
  }

  /**
   * Updates the optional last contact date.
   *
   * @param lastContactDate new last contact date
   */
  public void setLastContactDate(LocalDate lastContactDate) {
    this.lastContactDate = lastContactDate;
  }

  /**
   * Returns the optional next follow-up date.
   *
   * @return next follow-up date, may be {@code null}
   */
  public LocalDate getNextFollowUpDate() {
    return nextFollowUpDate;
  }

  /**
   * Updates the optional next follow-up date.
   *
   * @param nextFollowUpDate new next follow-up date
   */
  public void setNextFollowUpDate(LocalDate nextFollowUpDate) {
    this.nextFollowUpDate = nextFollowUpDate;
  }

  /**
   * Returns the recruiter relationship status.
   *
   * @return status
   */
  public RecruiterStatus getStatus() {
    return status;
  }

  /**
   * Updates the recruiter relationship status.
   *
   * @param status new status
   */
  public void setStatus(RecruiterStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
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
   * Reports whether this recruiter has been soft-deleted.
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

  /** Soft-deletes this recruiter. */
  public void archive() {
    this.archived = true;
    this.archivedAt = Instant.now();
  }
}
