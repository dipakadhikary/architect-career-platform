package com.acos.auth.validator;

import com.acos.auth.exception.WeakPasswordException;
import com.acos.common.api.ApiError;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/** Validates plain-text passwords against the platform password policy. */
@Component
public class PasswordValidator {

  static final int MIN_LENGTH = 12;
  static final int MAX_LENGTH = 72;

  private static final String FIELD = "password";
  private static final String POLICY_MESSAGE = "Password does not meet policy requirements";
  private static final Pattern UPPERCASE = Pattern.compile(".*\\p{Upper}.*");
  private static final Pattern LOWERCASE = Pattern.compile(".*\\p{Lower}.*");
  private static final Pattern DIGIT = Pattern.compile(".*\\p{Digit}.*");
  private static final Pattern SPECIAL = Pattern.compile(".*[^\\p{Alnum}].*");

  /**
   * Validates the given plain-text password.
   *
   * @param password candidate password
   * @throws WeakPasswordException when the password violates policy
   */
  public void validate(String password) {
    List<ApiError.FieldErrorDetail> details = collectViolations(password);
    if (!details.isEmpty()) {
      throw new WeakPasswordException(POLICY_MESSAGE, details);
    }
  }

  private static List<ApiError.FieldErrorDetail> collectViolations(String password) {
    if (password == null || password.isBlank()) {
      return List.of(ApiError.FieldErrorDetail.ofField(FIELD, "must not be blank"));
    }

    List<ApiError.FieldErrorDetail> details = new ArrayList<>();
    addLengthViolations(password, details);
    addCharacterClassViolations(password, details);
    return details;
  }

  private static void addLengthViolations(
      String password, List<ApiError.FieldErrorDetail> details) {
    if (password.length() < MIN_LENGTH) {
      details.add(
          ApiError.FieldErrorDetail.ofField(
              FIELD, "must be at least " + MIN_LENGTH + " characters"));
    }
    if (password.length() > MAX_LENGTH) {
      details.add(
          ApiError.FieldErrorDetail.ofField(
              FIELD, "must not exceed " + MAX_LENGTH + " characters"));
    }
  }

  private static void addCharacterClassViolations(
      String password, List<ApiError.FieldErrorDetail> details) {
    if (!UPPERCASE.matcher(password).matches()) {
      details.add(ApiError.FieldErrorDetail.ofField(FIELD, "must contain an uppercase letter"));
    }
    if (!LOWERCASE.matcher(password).matches()) {
      details.add(ApiError.FieldErrorDetail.ofField(FIELD, "must contain a lowercase letter"));
    }
    if (!DIGIT.matcher(password).matches()) {
      details.add(ApiError.FieldErrorDetail.ofField(FIELD, "must contain a digit"));
    }
    if (!SPECIAL.matcher(password).matches()) {
      details.add(ApiError.FieldErrorDetail.ofField(FIELD, "must contain a special character"));
    }
  }
}
