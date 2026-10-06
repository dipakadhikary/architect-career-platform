package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Human decision for a proposed high-risk action.
 *
 * @param decision APPROVE or REJECT
 */
@Schema(name = "AgentDecisionRequest", description = "Approve or reject a proposed agent action")
public record AgentDecisionRequest(@NotBlank @Pattern(regexp = "APPROVE|REJECT") String decision) {}
