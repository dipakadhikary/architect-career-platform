package com.acos.career.dto;

import com.acos.career.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload used to request a job application status transition.
 *
 * @param newStatus requested target status
 * @param comments optional comments describing the transition
 */
@Schema(
    name = "StatusTransitionRequest",
    description = "Payload used to request a job application status transition")
public record StatusTransitionRequest(
    @Schema(
            description = "Requested target status",
            example = "APPLIED",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "newStatus must not be null") ApplicationStatus newStatus,
    @Schema(
            description = "Optional comments describing the transition",
            example = "Submitted via referral",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "comments must not exceed 2000 characters") String comments) {}
