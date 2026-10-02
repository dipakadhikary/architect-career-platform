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
import java.util.Objects;

/** Interview scheduled for a job application, with AI-ready feedback fields. */
@Entity
@Table(name = "career_interviews", schema = "acos")
public class Interview extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "application_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_career_interviews_application_id"))
  private JobApplication application;

  @Enumerated(EnumType.STRING)
  @Column(name = "interview_round", nullable = false, length = 40)
  private InterviewRound interviewRound;

  @Column(name = "interviewer", length = 200)
  private String interviewer;

  @Column(name = "interview_date", nullable = false)
  private Instant interviewDate;

  @Column(name = "duration_minutes")
  private Integer durationMinutes;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private InterviewStatus status;

  @Column(name = "rating")
  private Integer rating;

  @Column(name = "feedback", length = 4000)
  private String feedback;

  @Column(name = "questions_asked", length = 10_000)
  private String questionsAsked;

  @Column(name = "strengths", length = 4000)
  private String strengths;

  @Column(name = "weaknesses", length = 4000)
  private String weaknesses;

  @Column(name = "improvement_areas", length = 4000)
  private String improvementAreas;

  @Column(name = "candidate_notes", length = 4000)
  private String candidateNotes;

  @Column(name = "confidence_rating")
  private Integer confidenceRating;

  @Column(name = "interview_reminder_date")
  private Instant interviewReminderDate;

  @Column(name = "location_or_link", length = 500)
  private String locationOrLink;

  @Column(name = "notes", length = 2000)
  private String notes;

  @Column(name = "archived", nullable = false)
  private boolean archived;

  @Column(name = "archived_at")
  private Instant archivedAt;

  /** Creates an empty interview for JPA. */
  protected Interview() {}

  /**
   * Creates an interview for a job application, always starting as {@link
   * InterviewStatus#SCHEDULED}.
   *
   * @param application owning application
   * @param interviewRound interview round
   * @param interviewDate scheduled interview time
   */
  public Interview(
      JobApplication application, InterviewRound interviewRound, Instant interviewDate) {
    this.application = Objects.requireNonNull(application, "application must not be null");
    this.interviewRound = Objects.requireNonNull(interviewRound, "interviewRound must not be null");
    this.interviewDate = Objects.requireNonNull(interviewDate, "interviewDate must not be null");
    this.status = InterviewStatus.SCHEDULED;
    this.archived = false;
  }

  /**
   * Returns the owning job application.
   *
   * @return application
   */
  public JobApplication getApplication() {
    return application;
  }

  /**
   * Returns the interview round.
   *
   * @return interview round
   */
  public InterviewRound getInterviewRound() {
    return interviewRound;
  }

  /**
   * Updates the interview round.
   *
   * @param interviewRound new interview round
   */
  public void setInterviewRound(InterviewRound interviewRound) {
    this.interviewRound = Objects.requireNonNull(interviewRound, "interviewRound must not be null");
  }

  /**
   * Returns the optional interviewer name.
   *
   * @return interviewer, may be {@code null}
   */
  public String getInterviewer() {
    return interviewer;
  }

  /**
   * Updates the optional interviewer name.
   *
   * @param interviewer new interviewer name
   */
  public void setInterviewer(String interviewer) {
    this.interviewer = interviewer;
  }

  /**
   * Returns the scheduled interview time.
   *
   * @return interview date
   */
  public Instant getInterviewDate() {
    return interviewDate;
  }

  /**
   * Updates the scheduled interview time.
   *
   * @param interviewDate new interview date
   */
  public void setInterviewDate(Instant interviewDate) {
    this.interviewDate = Objects.requireNonNull(interviewDate, "interviewDate must not be null");
  }

  /**
   * Returns the optional duration in minutes.
   *
   * @return duration minutes, may be {@code null}
   */
  public Integer getDurationMinutes() {
    return durationMinutes;
  }

  /**
   * Updates the optional duration in minutes.
   *
   * @param durationMinutes new duration minutes
   */
  public void setDurationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  /**
   * Returns the interview status.
   *
   * @return status
   */
  public InterviewStatus getStatus() {
    return status;
  }

  /**
   * Updates the interview status.
   *
   * @param status new status
   */
  public void setStatus(InterviewStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  /**
   * Returns the optional rating.
   *
   * @return rating from 1 to 5, may be {@code null}
   */
  public Integer getRating() {
    return rating;
  }

  /**
   * Updates the optional rating.
   *
   * @param rating new rating from 1 to 5
   */
  public void setRating(Integer rating) {
    this.rating = rating;
  }

  /**
   * Returns optional feedback.
   *
   * @return feedback, may be {@code null}
   */
  public String getFeedback() {
    return feedback;
  }

  /**
   * Updates optional feedback.
   *
   * @param feedback new feedback
   */
  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  /**
   * Returns the optional questions asked during the interview.
   *
   * @return questions asked, may be {@code null}
   */
  public String getQuestionsAsked() {
    return questionsAsked;
  }

  /**
   * Updates the optional questions asked during the interview.
   *
   * @param questionsAsked new questions asked
   */
  public void setQuestionsAsked(String questionsAsked) {
    this.questionsAsked = questionsAsked;
  }

  /**
   * Returns the optional observed strengths.
   *
   * @return strengths, may be {@code null}
   */
  public String getStrengths() {
    return strengths;
  }

  /**
   * Updates the optional observed strengths.
   *
   * @param strengths new strengths
   */
  public void setStrengths(String strengths) {
    this.strengths = strengths;
  }

  /**
   * Returns the optional observed weaknesses.
   *
   * @return weaknesses, may be {@code null}
   */
  public String getWeaknesses() {
    return weaknesses;
  }

  /**
   * Updates the optional observed weaknesses.
   *
   * @param weaknesses new weaknesses
   */
  public void setWeaknesses(String weaknesses) {
    this.weaknesses = weaknesses;
  }

  /**
   * Returns the optional improvement areas.
   *
   * @return improvement areas, may be {@code null}
   */
  public String getImprovementAreas() {
    return improvementAreas;
  }

  /**
   * Updates the optional improvement areas.
   *
   * @param improvementAreas new improvement areas
   */
  public void setImprovementAreas(String improvementAreas) {
    this.improvementAreas = improvementAreas;
  }

  /**
   * Returns the optional candidate self-assessment notes.
   *
   * @return candidate notes, may be {@code null}
   */
  public String getCandidateNotes() {
    return candidateNotes;
  }

  /**
   * Updates the optional candidate self-assessment notes.
   *
   * @param candidateNotes new candidate notes
   */
  public void setCandidateNotes(String candidateNotes) {
    this.candidateNotes = candidateNotes;
  }

  /**
   * Returns the optional candidate confidence rating.
   *
   * @return confidence rating from 1 to 5, may be {@code null}
   */
  public Integer getConfidenceRating() {
    return confidenceRating;
  }

  /**
   * Updates the optional candidate confidence rating.
   *
   * @param confidenceRating new confidence rating from 1 to 5
   */
  public void setConfidenceRating(Integer confidenceRating) {
    this.confidenceRating = confidenceRating;
  }

  /**
   * Returns the optional reminder time for this interview.
   *
   * @return interview reminder date, may be {@code null}
   */
  public Instant getInterviewReminderDate() {
    return interviewReminderDate;
  }

  /**
   * Updates the optional reminder time for this interview.
   *
   * @param interviewReminderDate new reminder time
   */
  public void setInterviewReminderDate(Instant interviewReminderDate) {
    this.interviewReminderDate = interviewReminderDate;
  }

  /**
   * Returns the optional location or meeting link.
   *
   * @return location or link, may be {@code null}
   */
  public String getLocationOrLink() {
    return locationOrLink;
  }

  /**
   * Updates the optional location or meeting link.
   *
   * @param locationOrLink new location or link
   */
  public void setLocationOrLink(String locationOrLink) {
    this.locationOrLink = locationOrLink;
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
   * Reports whether this interview has been soft-deleted.
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

  /** Soft-deletes this interview. */
  public void archive() {
    this.archived = true;
    this.archivedAt = Instant.now();
  }
}
