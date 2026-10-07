package com.acos.auth.controller;

import com.acos.auth.dto.EmailRecoveryRequest;
import com.acos.auth.dto.ResetPasswordRequest;
import com.acos.auth.recovery.AccountRecoveryService;
import com.acos.auth.recovery.RecoveryAcknowledgement;
import com.acos.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public account recovery endpoints. Responses do not reveal whether an email is registered. */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Account recovery")
public class AccountRecoveryController {

  private final AccountRecoveryService recoveryService;

  /**
   * Creates the controller.
   *
   * @param recoveryService recovery use-cases
   */
  public AccountRecoveryController(AccountRecoveryService recoveryService) {
    this.recoveryService = recoveryService;
  }

  /**
   * Emails the login identifier when an enabled account exists.
   *
   * @param request email address
   * @return generic acknowledgement
   */
  @PostMapping(
      path = "/forgot-user-id",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirements
  @Operation(
      summary = "Recover login identifier",
      description =
          "Sends the login email when an enabled account exists. The response is the same when"
              + " the address is unknown.")
  public ResponseEntity<ApiResponse<RecoveryAcknowledgement>> forgotUserId(
      @Valid @RequestBody EmailRecoveryRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(recoveryService.requestLoginIdentifier(request.email())));
  }

  /**
   * Emails a password reset link when an enabled account exists.
   *
   * @param request email address
   * @return generic acknowledgement
   */
  @PostMapping(
      path = "/forgot-password",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirements
  @Operation(
      summary = "Request a password reset",
      description =
          "Sends a single-use reset link when an enabled account exists. The response is the same"
              + " when the address is unknown.")
  public ResponseEntity<ApiResponse<RecoveryAcknowledgement>> forgotPassword(
      @Valid @RequestBody EmailRecoveryRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(recoveryService.requestPasswordReset(request.email())));
  }

  /**
   * Sets a new password for the account bound to the reset token.
   *
   * @param request token and new password
   * @return empty success envelope
   */
  @PostMapping(
      path = "/reset-password",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @SecurityRequirements
  @Operation(
      summary = "Reset password",
      description =
          "Consumes a single-use reset token, stores a new BCrypt password, and revokes refresh"
              + " tokens for that account.")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request) {
    recoveryService.resetPassword(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
