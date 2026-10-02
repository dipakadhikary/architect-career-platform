package com.acos.career.dto;

import com.acos.career.entity.RecruiterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Payload used to create or update a recruiter.
 *
 * @param companyId optional associated company id
 * @param fullName recruiter full name
 * @param email optional email
 * @param phone optional phone
 * @param linkedInUrl optional LinkedIn URL
 * @param lastContactDate optional last contact date
 * @param nextFollowUpDate optional next follow-up date
 * @param status optional relationship status, defaults to {@code ACTIVE} when omitted
 * @param notes optional notes
 */
@Schema(name = "RecruiterRequest", description = "Payload used to create or update a recruiter")
public record RecruiterRequest(
    @Schema(description = "Optional associated company identifier", nullable = true) UUID companyId,
    @Schema(
            description = "Recruiter full name",
            example = "Jamie Recruiter",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "fullName must not be blank") @Size(max = 200, message = "fullName must not exceed 200 characters") String fullName,
    @Schema(
            description = "Optional email",
            example = "jamie@agency.example.com",
            maxLength = 320,
            nullable = true)
        @Email(message = "email must be a valid email address") @Size(max = 320, message = "email must not exceed 320 characters") String email,
    @Schema(
            description = "Optional phone",
            example = "+1-555-0100",
            maxLength = 50,
            nullable = true)
        @Size(max = 50, message = "phone must not exceed 50 characters") String phone,
    @Schema(
            description = "Optional LinkedIn URL",
            example = "https://linkedin.com/in/jamie",
            maxLength = 500,
            nullable = true)
        @Size(max = 500, message = "linkedInUrl must not exceed 500 characters") String linkedInUrl,
    @Schema(description = "Optional last contact date", example = "2026-07-15", nullable = true)
        LocalDate lastContactDate,
    @Schema(description = "Optional next follow-up date", example = "2026-08-15", nullable = true)
        LocalDate nextFollowUpDate,
    @Schema(
            description = "Optional relationship status, defaults to ACTIVE",
            example = "ACTIVE",
            nullable = true)
        RecruiterStatus status,
    @Schema(
            description = "Optional notes",
            example = "Prefers email contact",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "notes must not exceed 2000 characters") String notes) {}
