package com.acos.tutorial.repository;

import com.acos.tutorial.entity.TutorialConcept;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorialConceptRepository extends JpaRepository<TutorialConcept, UUID> {

  Optional<TutorialConcept> findByTopicId(UUID topicId);

  boolean existsByTopicId(UUID topicId);
}
