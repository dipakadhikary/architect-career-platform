package com.acos.knowledge.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.knowledge.dto.KnowledgeNotePageResponse;
import com.acos.knowledge.dto.KnowledgeNoteRequest;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
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

/** REST API for knowledge note management. */
@RestController
@RequestMapping("/api/v1/knowledge/notes")
@Tag(name = "Knowledge", description = "Authenticated markdown knowledge note APIs")
@SecurityRequirement(name = "bearer-jwt")
public class KnowledgeController {

  private final KnowledgeService knowledgeService;

  /**
   * Creates the knowledge controller.
   *
   * @param knowledgeService knowledge service
   */
  public KnowledgeController(KnowledgeService knowledgeService) {
    this.knowledgeService = knowledgeService;
  }

  /**
   * Creates a knowledge note for the authenticated user.
   *
   * @param principal authenticated principal
   * @param request create payload
   * @return created note wrapped in {@link ApiResponse}
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Create knowledge note",
      description =
          "Creates a markdown knowledge note owned by the authenticated user. Optional category"
              + " and tags are created when missing.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Note created",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = KnowledgeNoteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<KnowledgeNoteResponse>> create(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody KnowledgeNoteRequest request) {
    KnowledgeNoteResponse response = knowledgeService.create(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  /**
   * Updates a knowledge note owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param noteId note id
   * @param request update payload
   * @return updated note wrapped in {@link ApiResponse}
   */
  @PutMapping(
      path = "/{noteId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Update knowledge note",
      description = "Updates a markdown knowledge note owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Note updated",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = KnowledgeNoteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Note not found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<KnowledgeNoteResponse>> update(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Knowledge note identifier") @PathVariable UUID noteId,
      @Valid @RequestBody KnowledgeNoteRequest request) {
    KnowledgeNoteResponse response = knowledgeService.update(principal.getId(), noteId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Deletes a knowledge note owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param noteId note id
   * @return success envelope
   */
  @DeleteMapping(path = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Delete knowledge note",
      description = "Deletes a markdown knowledge note owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Note deleted",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DeleteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Note not found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Knowledge note identifier") @PathVariable UUID noteId) {
    knowledgeService.delete(principal.getId(), noteId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * Returns a knowledge note owned by the authenticated user.
   *
   * @param principal authenticated principal
   * @param noteId note id
   * @return note wrapped in {@link ApiResponse}
   */
  @GetMapping(path = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get knowledge note",
      description = "Returns a markdown knowledge note owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Note returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = KnowledgeNoteApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Note not found",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<KnowledgeNoteResponse>> get(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Knowledge note identifier") @PathVariable UUID noteId) {
    KnowledgeNoteResponse response = knowledgeService.get(principal.getId(), noteId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists knowledge notes for the authenticated user.
   *
   * @param principal authenticated principal
   * @param pageable paging and sorting
   * @return page of notes wrapped in {@link ApiResponse}
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "List knowledge notes",
      description =
          "Returns a paginated list of markdown knowledge notes owned by the authenticated user.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Notes returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = KnowledgeNotePageApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<KnowledgeNotePageResponse>> list(
      @AuthenticationPrincipal AcosUserDetails principal,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    KnowledgeNotePageResponse response = knowledgeService.list(principal.getId(), pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Searches knowledge notes by title or summary for the authenticated user.
   *
   * @param principal authenticated principal
   * @param query search text
   * @param pageable paging and sorting
   * @return page of matching notes wrapped in {@link ApiResponse}
   */
  @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Search knowledge notes",
      description =
          "Searches markdown knowledge notes owned by the authenticated user by title or summary.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Matching notes returned",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = KnowledgeNotePageApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<KnowledgeNotePageResponse>> search(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Parameter(description = "Search text matched against title or summary", required = true)
          @RequestParam("q")
          String query,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    KnowledgeNotePageResponse response =
        knowledgeService.search(principal.getId(), query, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * OpenAPI schema for a successful note envelope.
   *
   * @param success whether the request succeeded
   * @param data note payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "KnowledgeNoteApiResponse", description = "Successful knowledge note envelope")
  public record KnowledgeNoteApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Knowledge note payload") KnowledgeNoteResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful page envelope.
   *
   * @param success whether the request succeeded
   * @param data page payload
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(
      name = "KnowledgeNotePageApiResponse",
      description = "Successful paginated knowledge notes envelope")
  public record KnowledgeNotePageApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Paginated notes payload") KnowledgeNotePageResponse data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for a successful delete envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on delete success
   * @param error always {@code null} on success
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "KnowledgeDeleteApiResponse", description = "Successful knowledge delete envelope")
  public record DeleteApiResponse(
      @Schema(description = "Whether the request succeeded", example = "true") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload", nullable = true) ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}

  /**
   * OpenAPI schema for an error envelope.
   *
   * @param success whether the request succeeded
   * @param data always {@code null} on failure
   * @param error error payload
   * @param correlationId request correlation identifier
   * @param timestamp response timestamp
   */
  @Schema(name = "KnowledgeErrorApiResponse", description = "Knowledge error response envelope")
  public record ErrorApiResponse(
      @Schema(description = "Whether the request succeeded", example = "false") boolean success,
      @Schema(description = "Success payload", nullable = true) Void data,
      @Schema(description = "Error payload") ApiError error,
      @Schema(description = "Request correlation identifier") String correlationId,
      @Schema(description = "Response timestamp", example = "2026-08-04T06:00:00Z")
          Instant timestamp) {}
}
