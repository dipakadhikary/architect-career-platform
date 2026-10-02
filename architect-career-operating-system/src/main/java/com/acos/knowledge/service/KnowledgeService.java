package com.acos.knowledge.service;

import com.acos.knowledge.dto.KnowledgeNotePageResponse;
import com.acos.knowledge.dto.KnowledgeNoteRequest;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/** Application service for knowledge note use-cases. */
public interface KnowledgeService {

  /**
   * Creates a knowledge note for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created note
   */
  KnowledgeNoteResponse create(UUID ownerId, KnowledgeNoteRequest request);

  /**
   * Updates a knowledge note owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param noteId note id
   * @param request update payload
   * @return updated note
   */
  KnowledgeNoteResponse update(UUID ownerId, UUID noteId, KnowledgeNoteRequest request);

  /**
   * Deletes a knowledge note owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param noteId note id
   */
  void delete(UUID ownerId, UUID noteId);

  /**
   * Returns a knowledge note owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param noteId note id
   * @return note
   */
  KnowledgeNoteResponse get(UUID ownerId, UUID noteId);

  /**
   * Lists knowledge notes for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param pageable paging and sorting
   * @return page of notes
   */
  KnowledgeNotePageResponse list(UUID ownerId, Pageable pageable);

  /**
   * Searches knowledge notes by title or summary for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param query search text
   * @param pageable paging and sorting
   * @return page of matching notes
   */
  KnowledgeNotePageResponse search(UUID ownerId, String query, Pageable pageable);
}
