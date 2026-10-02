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

  Optional<TutorialQuestion> findByIdAndTopic_OwnerId(UUID id, UUID ownerId);

  boolean existsByTopicId(UUID topicId);

  @Query(
      "select coalesce(max(q.sortOrder), -1) from TutorialQuestion q where q.topic.id = :topicId")
  int findMaxSortOrder(@Param("topicId") UUID topicId);
}
