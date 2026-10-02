package com.acos.tutorial.repository;

import java.util.List;
import java.util.UUID;

/** Native PostgreSQL full-text search for tutorials. */
public interface TutorialSearchRepository {

  record SearchHit(
      UUID topicId, String title, String path, String contentType, String snippet, double rank) {}

  List<SearchHit> search(UUID ownerId, String query, int limit, int offset);

  long count(UUID ownerId, String query);
}
