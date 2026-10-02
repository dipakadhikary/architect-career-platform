package com.acos.knowledge.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.common.exception.ErrorCode;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link KnowledgeNoteNotFoundException}. */
class KnowledgeNoteNotFoundExceptionTest {

  @Test
  void shouldExposeResourceNotFoundCode() {
    UUID noteId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    KnowledgeNoteNotFoundException exception = new KnowledgeNoteNotFoundException(noteId);

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    assertThat(exception.getMessage()).contains(noteId.toString());
  }
}
