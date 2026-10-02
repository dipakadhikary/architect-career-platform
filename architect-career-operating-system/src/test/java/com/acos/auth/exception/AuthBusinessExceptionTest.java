package com.acos.auth.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for auth business exceptions. */
class AuthBusinessExceptionTest {

  @Test
  void emailAlreadyExistsShouldUseConflictCode() {
    EmailAlreadyExistsException exception = new EmailAlreadyExistsException("ada@acos.local");

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    assertThat(exception.getMessage()).contains("ada@acos.local");
    assertThat(exception.getDetails()).isEmpty();
  }

  @Test
  void invalidPasswordShouldCarryFieldDetails() {
    List<ApiError.FieldErrorDetail> details =
        List.of(ApiError.FieldErrorDetail.ofField("password", "must contain a digit"));

    WeakPasswordException exception =
        new WeakPasswordException("Password does not meet policy requirements", details);

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.WEAK_PASSWORD);
    assertThat(exception.getDetails()).hasSize(1);
    assertThat(exception.getDetails().getFirst().field()).isEqualTo("password");
  }

  @Test
  void roleNotFoundShouldUseNotFoundCode() {
    RoleNotFoundException exception = new RoleNotFoundException(com.acos.auth.entity.RoleType.USER);

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND);
    assertThat(exception.getMessage()).contains("USER");
  }

  @Test
  void invalidCredentialsShouldUseUnauthorizedCode() {
    InvalidCredentialsException exception = new InvalidCredentialsException();

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
  }

  @Test
  void accountDisabledShouldUseForbiddenCode() {
    AccountDisabledException exception = new AccountDisabledException();

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_DISABLED);
    assertThat(exception.getMessage()).isEqualTo("Account is disabled");
  }

  @Test
  void invalidTokenShouldUseUnauthorizedCode() {
    InvalidTokenException exception = new InvalidTokenException();

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    assertThat(exception.getMessage()).isEqualTo("Invalid or expired token");
  }
}
