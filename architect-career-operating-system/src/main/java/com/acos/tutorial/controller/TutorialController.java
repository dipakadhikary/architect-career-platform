package com.acos.tutorial.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.tutorial.dto.TutorialConceptRequest;
import com.acos.tutorial.dto.TutorialConceptResponse;
import com.acos.tutorial.dto.TutorialQuestionRequest;
import com.acos.tutorial.dto.TutorialQuestionResponse;
import com.acos.tutorial.dto.TutorialQuestionsPageResponse;
import com.acos.tutorial.dto.TutorialSearchPageResponse;
import com.acos.tutorial.dto.TutorialTopicRequest;
import com.acos.tutorial.dto.TutorialTopicResponse;
import com.acos.tutorial.dto.TutorialTreeNodeResponse;
import com.acos.tutorial.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST API for hierarchical tutorials. */
@RestController
@RequestMapping("/api/v1/tutorials")
@Tag(name = "Tutorials", description = "Authenticated hierarchical learning material APIs")
@SecurityRequirement(name = "bearer-jwt")
public class TutorialController {

  private final TutorialService tutorialService;

  public TutorialController(TutorialService tutorialService) {
    this.tutorialService = tutorialService;
  }

  @GetMapping(value = "/tree", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get tutorial hierarchy tree for sidebar navigation")
  public ApiResponse<List<TutorialTreeNodeResponse>> tree(
      @AuthenticationPrincipal AcosUserDetails principal) {
    return ApiResponse.success(tutorialService.getTree(principal.getId()));
  }

  @PostMapping(
      value = "/topics",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create tutorial topic")
  public ResponseEntity<ApiResponse<TutorialTopicResponse>> createTopic(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody TutorialTopicRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(tutorialService.createTopic(principal.getId(), request)));
  }

  @PutMapping(
      value = "/topics/{topicId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update tutorial topic (title, slug, parent, sort order)")
  public ApiResponse<TutorialTopicResponse> updateTopic(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable UUID topicId,
      @Valid @RequestBody TutorialTopicRequest request) {
    return ApiResponse.success(tutorialService.updateTopic(principal.getId(), topicId, request));
  }

  @DeleteMapping(value = "/topics/{topicId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete tutorial topic and cascading children/content")
  public ApiResponse<Void> deleteTopic(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable UUID topicId) {
    tutorialService.deleteTopic(principal.getId(), topicId);
    return ApiResponse.success(null);
  }

  @GetMapping(value = "/topics/by-path", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get topic metadata by stable path")
  public ApiResponse<TutorialTopicResponse> getByPath(
      @AuthenticationPrincipal AcosUserDetails principal, @RequestParam("path") String path) {
    return ApiResponse.success(tutorialService.getTopicByPath(principal.getId(), path));
  }

  @GetMapping(value = "/topics/by-path/concept", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get concept markdown by topic path")
  public ApiResponse<TutorialConceptResponse> getConceptByPath(
      @AuthenticationPrincipal AcosUserDetails principal, @RequestParam("path") String path) {
    return ApiResponse.success(tutorialService.getConceptByPath(principal.getId(), path));
  }

  @PutMapping(
      value = "/topics/{topicId}/concept",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create or update concept markdown for a topic")
  public ApiResponse<TutorialConceptResponse> upsertConcept(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable UUID topicId,
      @Valid @RequestBody TutorialConceptRequest request) {
    return ApiResponse.success(tutorialService.upsertConcept(principal.getId(), topicId, request));
  }

  @GetMapping(value = "/topics/by-path/questions", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List questions and answers by topic path")
  public ApiResponse<TutorialQuestionsPageResponse> getQuestionsByPath(
      @AuthenticationPrincipal AcosUserDetails principal, @RequestParam("path") String path) {
    return ApiResponse.success(tutorialService.getQuestionsByPath(principal.getId(), path));
  }

  @PostMapping(
      value = "/topics/{topicId}/questions",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a question/answer pair")
  public ResponseEntity<ApiResponse<TutorialQuestionResponse>> createQuestion(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable UUID topicId,
      @Valid @RequestBody TutorialQuestionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponse.success(
                tutorialService.createQuestion(principal.getId(), topicId, request)));
  }

  @PutMapping(
      value = "/questions/{questionId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update a question/answer pair")
  public ApiResponse<TutorialQuestionResponse> updateQuestion(
      @AuthenticationPrincipal AcosUserDetails principal,
      @PathVariable UUID questionId,
      @Valid @RequestBody TutorialQuestionRequest request) {
    return ApiResponse.success(
        tutorialService.updateQuestion(principal.getId(), questionId, request));
  }

  @DeleteMapping(value = "/questions/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete a question/answer pair")
  public ApiResponse<Void> deleteQuestion(
      @AuthenticationPrincipal AcosUserDetails principal, @PathVariable UUID questionId) {
    tutorialService.deleteQuestion(principal.getId(), questionId);
    return ApiResponse.success(null);
  }

  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Full-text search concepts, questions, and answers (PostgreSQL FTS)")
  public ApiResponse<TutorialSearchPageResponse> search(
      @AuthenticationPrincipal AcosUserDetails principal,
      @RequestParam("q") String query,
      @ParameterObject @PageableDefault(size = 20, sort = "rank", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ApiResponse.success(tutorialService.search(principal.getId(), query, pageable));
  }
}
