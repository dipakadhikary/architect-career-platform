#!/usr/bin/env python3
"""Generate tutorial repositories."""
from pathlib import Path

BASE = Path(r"D:\architect-career-system\architect-career-operating-system\src\main\java\com\acos\tutorial")


def w(rel: str, content: str) -> None:
    path = BASE / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")
    print(rel)


w("repository/TutorialTopicRepository.java", r"""
package com.acos.tutorial.repository;

import com.acos.tutorial.entity.TutorialTopic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorialTopicRepository extends JpaRepository<TutorialTopic, UUID> {

  Optional<TutorialTopic> findByIdAndOwnerId(UUID id, UUID ownerId);

  Optional<TutorialTopic> findByOwnerIdAndPath(UUID ownerId, String path);

  List<TutorialTopic> findByOwnerIdOrderByPathAsc(UUID ownerId);

  List<TutorialTopic> findByOwnerIdAndParentIsNullOrderBySortOrderAsc(UUID ownerId);

  List<TutorialTopic> findByOwnerIdAndParentIdOrderBySortOrderAsc(UUID ownerId, UUID parentId);

  @Query(
      """
      select coalesce(max(t.sortOrder), -1) from TutorialTopic t
      where t.ownerId = :ownerId
        and ((:parentId is null and t.parent is null) or t.parent.id = :parentId)
      """)
  int findMaxSortOrder(@Param("ownerId") UUID ownerId, @Param("parentId") UUID parentId);

  @Query(
      """
      select count(t) > 0 from TutorialTopic t
      where t.ownerId = :ownerId and t.path = :path
        and (:excludeId is null or t.id <> :excludeId)
      """)
  boolean existsPath(
      @Param("ownerId") UUID ownerId, @Param("path") String path, @Param("excludeId") UUID excludeId);

  @Query(
      """
      select t from TutorialTopic t
      where t.ownerId = :ownerId and t.path like concat(:pathPrefix, '/%')
      order by t.path asc
      """)
  List<TutorialTopic> findDescendantsByPathPrefix(
      @Param("ownerId") UUID ownerId, @Param("pathPrefix") String pathPrefix);

  long countByOwnerIdAndParentId(UUID ownerId, UUID parentId);

  long countByOwnerIdAndParentIsNull(UUID ownerId);
}
""")

w("repository/TutorialConceptRepository.java", r"""
package com.acos.tutorial.repository;

import com.acos.tutorial.entity.TutorialConcept;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorialConceptRepository extends JpaRepository<TutorialConcept, UUID> {

  Optional<TutorialConcept> findByTopicId(UUID topicId);

  boolean existsByTopicId(UUID topicId);

  void deleteByTopicId(UUID topicId);
}
""")

w("repository/TutorialQuestionRepository.java", r"""
package com.acos.tutorial.repository;

import com.acos.tutorial.entity.TutorialQuestion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorialQuestionRepository extends JpaRepository<TutorialQuestion, UUID> {

  List<TutorialQuestion> findByTopicIdOrderBySortOrderAsc(UUID topicId);

  Optional<TutorialQuestion> findByIdAndTopicOwnerId(UUID id, UUID ownerId);

  boolean existsByTopicId(UUID topicId);

  long countByTopicId(UUID topicId);

  @Query("select coalesce(max(q.sortOrder), -1) from TutorialQuestion q where q.topic.id = :topicId")
  int findMaxSortOrder(@Param("topicId") UUID topicId);

  void deleteByTopicId(UUID topicId);
}
""")

w("repository/TutorialSearchRepository.java", r"""
package com.acos.tutorial.repository;

import java.util.List;
import java.util.UUID;

/** Native PostgreSQL full-text search for tutorials. */
public interface TutorialSearchRepository {

  record SearchHit(
      UUID topicId,
      String title,
      String path,
      String contentType,
      String snippet,
      double rank) {}

  List<SearchHit> search(UUID ownerId, String query, int limit, int offset);

  long count(UUID ownerId, String query);
}
""")

w("repository/TutorialSearchRepositoryImpl.java", r"""
package com.acos.tutorial.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TutorialSearchRepositoryImpl implements TutorialSearchRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  @SuppressWarnings("unchecked")
  public List<SearchHit> search(UUID ownerId, String query, int limit, int offset) {
    Query nativeQuery =
        entityManager.createNativeQuery(
            """
            select topic_id, title, path, content_type, snippet, rank
            from (
              select
                t.id as topic_id,
                t.title as title,
                t.path as path,
                'CONCEPT' as content_type,
                ts_headline(
                  'english',
                  c.content,
                  websearch_to_tsquery('english', :q),
                  'StartSel=<mark>, StopSel=</mark>, MaxWords=28, MinWords=12, MaxFragments=1'
                ) as snippet,
                ts_rank(c.search_vector, websearch_to_tsquery('english', :q)) as rank
              from acos.tutorial_concepts c
              join acos.tutorial_topics t on t.id = c.topic_id
              where t.owner_id = :ownerId
                and c.search_vector @@ websearch_to_tsquery('english', :q)
              union all
              select
                t.id as topic_id,
                t.title as title,
                t.path as path,
                'QUESTIONS_AND_ANSWERS' as content_type,
                ts_headline(
                  'english',
                  q.question || ' ' || q.answer,
                  websearch_to_tsquery('english', :q),
                  'StartSel=<mark>, StopSel=</mark>, MaxWords=28, MinWords=12, MaxFragments=1'
                ) as snippet,
                ts_rank(q.search_vector, websearch_to_tsquery('english', :q)) as rank
              from acos.tutorial_questions q
              join acos.tutorial_topics t on t.id = q.topic_id
              where t.owner_id = :ownerId
                and q.search_vector @@ websearch_to_tsquery('english', :q)
            ) hits
            order by rank desc, title asc
            limit :limit offset :offset
            """);
    nativeQuery.setParameter("ownerId", ownerId);
    nativeQuery.setParameter("q", query);
    nativeQuery.setParameter("limit", limit);
    nativeQuery.setParameter("offset", offset);

    List<Object[]> rows = nativeQuery.getResultList();
    List<SearchHit> hits = new ArrayList<>(rows.size());
    for (Object[] row : rows) {
      hits.add(
          new SearchHit(
              (UUID) row[0],
              (String) row[1],
              (String) row[2],
              (String) row[3],
              (String) row[4],
              row[5] == null ? 0.0 : ((Number) row[5]).doubleValue()));
    }
    return hits;
  }

  @Override
  public long count(UUID ownerId, String query) {
    Query nativeQuery =
        entityManager.createNativeQuery(
            """
            select count(*) from (
              select c.id
              from acos.tutorial_concepts c
              join acos.tutorial_topics t on t.id = c.topic_id
              where t.owner_id = :ownerId
                and c.search_vector @@ websearch_to_tsquery('english', :q)
              union all
              select q.id
              from acos.tutorial_questions q
              join acos.tutorial_topics t on t.id = q.topic_id
              where t.owner_id = :ownerId
                and q.search_vector @@ websearch_to_tsquery('english', :q)
            ) counted
            """);
    nativeQuery.setParameter("ownerId", ownerId);
    nativeQuery.setParameter("q", query);
    Number count = (Number) nativeQuery.getSingleResult();
    return count.longValue();
  }
}
""")

print("repos done")
