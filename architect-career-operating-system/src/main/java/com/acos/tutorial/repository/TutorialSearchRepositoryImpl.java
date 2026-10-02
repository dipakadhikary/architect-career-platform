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
                ts_rank(
                  to_tsvector('english', coalesce(c.content, '')),
                  websearch_to_tsquery('english', :q)
                ) as rank
              from acos.tutorial_concepts c
              join acos.tutorial_topics t on t.id = c.topic_id
              where t.owner_id = :ownerId
                and to_tsvector('english', coalesce(c.content, ''))
                    @@ websearch_to_tsquery('english', :q)
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
                ts_rank(
                  to_tsvector(
                    'english',
                    coalesce(q.question, '') || ' ' || coalesce(q.answer, '')
                  ),
                  websearch_to_tsquery('english', :q)
                ) as rank
              from acos.tutorial_questions q
              join acos.tutorial_topics t on t.id = q.topic_id
              where t.owner_id = :ownerId
                and to_tsvector(
                      'english',
                      coalesce(q.question, '') || ' ' || coalesce(q.answer, '')
                    ) @@ websearch_to_tsquery('english', :q)
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
                and to_tsvector('english', coalesce(c.content, ''))
                    @@ websearch_to_tsquery('english', :q)
              union all
              select q.id
              from acos.tutorial_questions q
              join acos.tutorial_topics t on t.id = q.topic_id
              where t.owner_id = :ownerId
                and to_tsvector(
                      'english',
                      coalesce(q.question, '') || ' ' || coalesce(q.answer, '')
                    ) @@ websearch_to_tsquery('english', :q)
            ) counted
            """);
    nativeQuery.setParameter("ownerId", ownerId);
    nativeQuery.setParameter("q", query);
    Number count = (Number) nativeQuery.getSingleResult();
    return count.longValue();
  }
}
