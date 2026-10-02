package com.acos.tutorial.exception;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import java.util.List;

public class TutorialCircularHierarchyException extends ValidationException {

  private static final long serialVersionUID = 1L;

  public TutorialCircularHierarchyException() {
    super(
        "Circular tutorial hierarchy is not allowed",
        List.of(ApiError.FieldErrorDetail.ofField("parentId", "would create a cycle")));
  }
}
