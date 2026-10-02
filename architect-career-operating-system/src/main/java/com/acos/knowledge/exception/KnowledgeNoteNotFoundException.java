package com.acos.knowledge.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a knowledge note cannot be found for the authenticated owner. */
public class KnowledgeNoteNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception for a note id.
   *
   * @param noteId missing note id
   */
  public KnowledgeNoteNotFoundException(UUID noteId) {
    super(ErrorCode.RESOURCE_NOT_FOUND, "KnowledgeNote not found with identifier '" + noteId + "'");
  }
}
