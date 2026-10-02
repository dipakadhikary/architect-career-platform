package com.acos.auth.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.auth.exception.WeakPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Unit tests for {@link PasswordValidator}. */
class PasswordValidatorTest {

  private PasswordValidator passwordValidator;

  @BeforeEach
  void setUp() {
    passwordValidator = new PasswordValidator();
  }

  @Test
  void shouldAcceptPasswordMeetingPolicy() {
    assertThatCode(() -> passwordValidator.validate("Str0ng!Pass12")).doesNotThrowAnyException();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void shouldRejectBlankPassword(String password) {
    assertThatThrownBy(() -> passwordValidator.validate(password))
        .isInstanceOf(WeakPasswordException.class)
        .hasMessageContaining("Password does not meet policy requirements");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Short1!",
        "Str0ng!Pa1",
        "alllowercase1!",
        "ALLUPPERCASE1!",
        "NoDigits!!Aa",
        "NoSpecial1Abc",
        "ThisPasswordIsWayTooLongForBcryptBecauseItExceedsSeventyTwoCharacters!!!!!Extra"
      })
  void shouldRejectPasswordsThatViolatePolicy(String password) {
    assertThatThrownBy(() -> passwordValidator.validate(password))
        .isInstanceOf(WeakPasswordException.class);
  }
}
