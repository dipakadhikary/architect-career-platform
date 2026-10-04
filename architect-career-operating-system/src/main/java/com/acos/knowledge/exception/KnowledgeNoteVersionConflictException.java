package com.acos.knowledge.exception;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.util.UUID;

/** Raised when a note save does not match the version the caller reviewed. */
public class KnowledgeNoteVersionConflictException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a conflict for a note that changed after it was loaded.
   *
   * @param noteId note id
   */
  public KnowledgeNoteVersionConflictException(UUID noteId) {
    super(
        ErrorCode.VERSION_CONFLICT,
        "KnowledgeNote '" + noteId + "' changed. Reload the latest note before saving.");
  }
}
