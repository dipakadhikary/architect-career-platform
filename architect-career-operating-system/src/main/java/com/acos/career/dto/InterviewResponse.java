package com.acos.career.dto;

import com.acos.career.entity.InterviewRound;
import com.acos.career.entity.InterviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Interview returned by the career APIs.
 *
 * @param id interview id
 * @param applicationId owning application id
 * @param interviewRound interview round
 * @param interviewer optional interviewer name
 * @param interviewDate scheduled interview time
 * @param durationMinutes optional duration in minutes
 * @param status interview status
 * @param rating optional rating
 * @param feedback optional feedback
 * @param questionsAsked optional questions asked
 * @param strengths optional observed strengths
 * @param weaknesses optional observed weaknesses
 * @param improvementAreas optional improvement areas
 * @param candidateNotes optional candidate self-assessment notes
 * @param confidenceRating optional candidate confidence rating
 * @param interviewReminderDate optional reminder time
 * @param locationOrLink optional location or meeting link
 * @param notes optional notes
 * @param archived whether the interview has been archived
 * @param archivedAt optional archival timestamp
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 * @param version optimistic locking version
 */
@Schema(name = "InterviewResponse", description = "Scheduled interview")
public record InterviewResponse(
    @Schema(description = "Interview identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Owning application identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID applicationId,
    @Schema(description = "Interview round", requiredMode = Schema.RequiredMode.REQUIRED)
        InterviewRound interviewRound,
    @Schema(description = "Optional interviewer name", nullable = true) String interviewer,
    @Schema(description = "Scheduled interview time", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant interviewDate,
    @Schema(description = "Optional duration in minutes", nullable = true) Integer durationMinutes,
    @Schema(description = "Interview status", requiredMode = Schema.RequiredMode.REQUIRED)
        InterviewStatus status,
    @Schema(description = "Optional rating from 1 to 5", nullable = true) Integer rating,
    @Schema(description = "Optional feedback", nullable = true) String feedback,
    @Schema(description = "Optional questions asked", nullable = true) String questionsAsked,
    @Schema(description = "Optional observed strengths", nullable = true) String strengths,
    @Schema(description = "Optional observed weaknesses", nullable = true) String weaknesses,
    @Schema(description = "Optional improvement areas", nullable = true) String improvementAreas,
    @Schema(description = "Optional candidate self-assessment notes", nullable = true)
        String candidateNotes,
    @Schema(description = "Optional candidate confidence rating from 1 to 5", nullable = true)
        Integer confidenceRating,
    @Schema(description = "Optional reminder time", nullable = true) Instant interviewReminderDate,
    @Schema(description = "Optional location or meeting link", nullable = true)
        String locationOrLink,
    @Schema(description = "Optional notes", nullable = true) String notes,
    @Schema(
            description = "Whether the interview is archived",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean archived,
    @Schema(description = "Optional archival timestamp", nullable = true) Instant archivedAt,
    @Schema(description = "Creation timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt,
    @Schema(description = "Optimistic locking version", requiredMode = Schema.RequiredMode.REQUIRED)
        long version) {

  /**
   * Creates an immutable interview response.
   *
   * @param id interview id
   * @param applicationId application id
   * @param interviewRound interview round
   * @param interviewer interviewer name
   * @param interviewDate interview date
   * @param durationMinutes duration minutes
   * @param status status
   * @param rating rating
   * @param feedback feedback
   * @param questionsAsked questions asked
   * @param strengths strengths
   * @param weaknesses weaknesses
   * @param improvementAreas improvement areas
   * @param candidateNotes candidate notes
   * @param confidenceRating confidence rating
   * @param interviewReminderDate reminder date
   * @param locationOrLink location or link
   * @param notes notes
   * @param archived archived flag
   * @param archivedAt archived-at timestamp
   * @param createdAt creation timestamp
   * @param updatedAt update timestamp
   * @param version optimistic locking version
   */
  public InterviewResponse {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(interviewRound, "interviewRound must not be null");
    Objects.requireNonNull(interviewDate, "interviewDate must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }
}
