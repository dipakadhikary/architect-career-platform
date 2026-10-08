package com.acos.notification.web;

import com.acos.common.api.ApiResponse;
import com.acos.notification.template.MessageTemplateResponse;
import com.acos.notification.template.MessageTemplateService;
import com.acos.notification.template.SaveMessageTemplateRequest;
import com.acos.notification.template.TemplatePreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Template designer API for email and SMS messages. */
@RestController
@RequestMapping("/api/v1/notifications/templates")
@Tag(name = "Notifications", description = "Email and SMS templates")
public class MessageTemplateController {

  private final MessageTemplateService templates;

  /**
   * Creates the controller.
   *
   * @param templates template use-cases
   */
  public MessageTemplateController(MessageTemplateService templates) {
    this.templates = templates;
  }

  /**
   * Lists stored templates.
   *
   * @return templates
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List message templates")
  public ResponseEntity<ApiResponse<List<MessageTemplateResponse>>> list() {
    return ResponseEntity.ok(ApiResponse.success(templates.list()));
  }

  /**
   * Creates a template.
   *
   * @param request template fields
   * @return saved template
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a message template")
  public ResponseEntity<ApiResponse<MessageTemplateResponse>> create(
      @Valid @RequestBody SaveMessageTemplateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(templates.create(request)));
  }

  /**
   * Updates a template design.
   *
   * @param id template id
   * @param request replacement fields
   * @return saved template
   */
  @PutMapping(
      path = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update a message template")
  public ResponseEntity<ApiResponse<MessageTemplateResponse>> update(
      @PathVariable UUID id, @Valid @RequestBody SaveMessageTemplateRequest request) {
    return ResponseEntity.ok(ApiResponse.success(templates.update(id, request)));
  }

  /**
   * Renders the stored design with sample data.
   *
   * @param id template id
   * @return rendered subject and bodies
   */
  @PostMapping(path = "/{id}/preview", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Preview a message template")
  public ResponseEntity<ApiResponse<TemplatePreviewResponse>> preview(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(templates.preview(id)));
  }
}
