package com.acos.tutorial.exception;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import java.util.List;

public class TutorialDuplicatePathException extends ValidationException {

  private static final long serialVersionUID = 1L;

  public TutorialDuplicatePathException(String path) {
    super(
        "Tutorial path already exists: " + path,
        List.of(ApiError.FieldErrorDetail.ofField("path", "must be unique for the owner")));
  }
}
