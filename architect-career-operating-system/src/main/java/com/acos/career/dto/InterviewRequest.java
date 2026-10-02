package com.acos.career.dto;

import com.acos.career.entity.InterviewRound;
import com.acos.career.entity.InterviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/**
 * Payload used to create or update an interview. On creation the interview always starts as {@code
 * SCHEDULED}; the {@code status} field is honored only for updates.
 *
 * @param interviewRound interview round
 * @param interviewer optional interviewer name
 * @param interviewDate scheduled interview time
 * @param durationMinutes optional duration in minutes
 * @param status interview status
 * @param rating optional rating from 1 to 5
 * @param feedback optional feedback
 * @param questionsAsked optional questions asked
 * @param strengths optional observed strengths
 * @param weaknesses optional observed weaknesses
 * @param improvementAreas optional improvement areas
 * @param candidateNotes optional candidate self-assessment notes
 * @param confidenceRating optional candidate confidence rating from 1 to 5
 * @param interviewReminderDate optional reminder time
 * @param locationOrLink optional location or meeting link
 * @param notes optional notes
 */
@Schema(name = "InterviewRequest", description = "Payload used to create or update an interview")
public record InterviewRequest(
    @Schema(
            description = "Interview round",
            example = "TECHNICAL",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "interviewRound must not be null") InterviewRound interviewRound,
    @Schema(
            description = "Optional interviewer name",
            example = "Alex Manager",
            maxLength = 200,
            nullable = true)
        @Size(max = 200, message = "interviewer must not exceed 200 characters") String interviewer,
    @Schema(
            description = "Scheduled interview time",
            example = "2026-08-10T15:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "interviewDate must not be null") Instant interviewDate,
    @Schema(description = "Optional duration in minutes", example = "60", nullable = true)
        @Positive(message = "durationMinutes must be positive") Integer durationMinutes,
    @Schema(
            description = "Interview status, honored only on update",
            example = "SCHEDULED",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "status must not be null") InterviewStatus status,
    @Schema(description = "Optional rating from 1 to 5", example = "4", nullable = true)
        @Min(value = 1, message = "rating must be at least 1") @Max(value = 5, message = "rating must be at most 5") Integer rating,
    @Schema(
            description = "Optional feedback",
            example = "Strong system design answers",
            maxLength = 4000,
            nullable = true)
        @Size(max = 4000, message = "feedback must not exceed 4000 characters") String feedback,
    @Schema(description = "Optional questions asked", maxLength = 10_000, nullable = true)
        @Size(max = 10_000, message = "questionsAsked must not exceed 10000 characters") String questionsAsked,
    @Schema(description = "Optional observed strengths", maxLength = 4000, nullable = true)
        @Size(max = 4000, message = "strengths must not exceed 4000 characters") String strengths,
    @Schema(description = "Optional observed weaknesses", maxLength = 4000, nullable = true)
        @Size(max = 4000, message = "weaknesses must not exceed 4000 characters") String weaknesses,
    @Schema(description = "Optional improvement areas", maxLength = 4000, nullable = true)
        @Size(max = 4000, message = "improvementAreas must not exceed 4000 characters") String improvementAreas,
    @Schema(
            description = "Optional candidate self-assessment notes",
            maxLength = 4000,
            nullable = true)
        @Size(max = 4000, message = "candidateNotes must not exceed 4000 characters") String candidateNotes,
    @Schema(
            description = "Optional candidate confidence rating from 1 to 5",
            example = "3",
            nullable = true)
        @Min(value = 1, message = "confidenceRating must be at least 1") @Max(value = 5, message = "confidenceRating must be at most 5") Integer confidenceRating,
    @Schema(description = "Optional reminder time", nullable = true) Instant interviewReminderDate,
    @Schema(
            description = "Optional location or meeting link",
            example = "https://meet.example.com/abc",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "locationOrLink must not exceed 500 characters") String locationOrLink,
    @Schema(
            description = "Optional notes",
            example = "Prepare distributed systems examples",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "notes must not exceed 2000 characters") String notes) {}
