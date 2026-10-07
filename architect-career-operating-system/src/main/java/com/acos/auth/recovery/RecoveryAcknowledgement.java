package com.acos.auth.recovery;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Public recovery response. The message is the same whether or not an account exists.
 *
 * @param message generic instruction
 */
@Schema(name = "RecoveryAcknowledgement", description = "Generic account recovery acknowledgement")
public record RecoveryAcknowledgement(
    @Schema(
            description = "Generic recovery message",
            example = RecoveryAcknowledgement.MESSAGE)
        String message) {

  /** Message returned for every public recovery request. */
  public static final String MESSAGE =
      "If an account exists for this email address, further instructions have been sent.";

  /**
   * Returns the standard acknowledgement.
   *
   * @return generic response
   */
  public static RecoveryAcknowledgement generic() {
    return new RecoveryAcknowledgement(MESSAGE);
  }
}
