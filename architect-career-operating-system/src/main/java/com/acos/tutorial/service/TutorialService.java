package com.acos.tutorial.service;

import com.acos.tutorial.dto.TutorialConceptRequest;
import com.acos.tutorial.dto.TutorialConceptResponse;
import com.acos.tutorial.dto.TutorialQuestionRequest;
import com.acos.tutorial.dto.TutorialQuestionResponse;
import com.acos.tutorial.dto.TutorialQuestionsPageResponse;
import com.acos.tutorial.dto.TutorialSearchPageResponse;
import com.acos.tutorial.dto.TutorialTopicRequest;
import com.acos.tutorial.dto.TutorialTopicResponse;
import com.acos.tutorial.dto.TutorialTreeNodeResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/** Tutorial hierarchy, content, and search operations. */
public interface TutorialService {

  List<TutorialTreeNodeResponse> getTree(UUID ownerId);

  TutorialTopicResponse createTopic(UUID ownerId, TutorialTopicRequest request);

  TutorialTopicResponse updateTopic(UUID ownerId, UUID topicId, TutorialTopicRequest request);

  void deleteTopic(UUID ownerId, UUID topicId);

  TutorialTopicResponse getTopicByPath(UUID ownerId, String path);

  TutorialConceptResponse getConceptByPath(UUID ownerId, String path);

  TutorialConceptResponse upsertConcept(UUID ownerId, UUID topicId, TutorialConceptRequest request);

  TutorialQuestionsPageResponse getQuestionsByPath(UUID ownerId, String path);

  TutorialQuestionResponse createQuestion(
      UUID ownerId, UUID topicId, TutorialQuestionRequest request);

  TutorialQuestionResponse updateQuestion(
      UUID ownerId, UUID questionId, TutorialQuestionRequest request);

  void deleteQuestion(UUID ownerId, UUID questionId);

  TutorialSearchPageResponse search(UUID ownerId, String query, Pageable pageable);
}
