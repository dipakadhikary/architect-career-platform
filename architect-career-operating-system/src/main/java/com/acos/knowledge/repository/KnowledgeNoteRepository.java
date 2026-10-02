package com.acos.knowledge.repository;

import com.acos.knowledge.entity.KnowledgeNote;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link KnowledgeNote}. */
public interface KnowledgeNoteRepository extends JpaRepository<KnowledgeNote, UUID> {

  /**
   * Finds a note by id and owner, eagerly loading category and tags.
   *
   * @param id note id
   * @param ownerId owner id
   * @return matching note, if present
   */
  @EntityGraph(attributePaths = {"category", "tags"})
  Optional<KnowledgeNote> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Lists notes for an owner, eagerly loading category. Tags are batch-loaded.
   *
   * @param ownerId owner id
   * @param pageable paging
   * @return page of notes
   */
  @EntityGraph(attributePaths = {"category"})
  Page<KnowledgeNote> findByOwnerId(UUID ownerId, Pageable pageable);

  /**
   * Searches notes by title or summary for an owner. Tags are batch-loaded.
   *
   * @param ownerId owner id
   * @param query search text
   * @param pageable paging
   * @return page of matching notes
   */
  @EntityGraph(attributePaths = {"category"})
  @Query(
      """
      select n from KnowledgeNote n
      where n.ownerId = :ownerId
        and (lower(n.title) like lower(concat('%', :query, '%'))
          or lower(n.summary) like lower(concat('%', :query, '%')))
      """)
  Page<KnowledgeNote> searchByOwnerIdAndTitleOrSummary(
      @Param("ownerId") UUID ownerId, @Param("query") String query, Pageable pageable);
}
