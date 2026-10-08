package com.acos.notification.template;

import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import com.acos.notification.api.NotificationChannel;
import com.acos.notification.api.RenderedNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Stores and previews message templates. */
@Service
public class MessageTemplateService {

  private static final Pattern CODE = Pattern.compile("[A-Z0-9_]{1,64}");

  private final MessageTemplateRepository repository;
  private final TemplateRenderer renderer;
  private final ObjectMapper objectMapper;

  /**
   * Creates the service.
   *
   * @param repository template store
   * @param renderer placeholder renderer
   * @param objectMapper JSON parser for sample data
   */
  public MessageTemplateService(
      MessageTemplateRepository repository, TemplateRenderer renderer, ObjectMapper objectMapper) {
    this.repository = repository;
    this.renderer = renderer;
    this.objectMapper = objectMapper;
  }

  /**
   * Lists templates for the designer.
   *
   * @return templates
   */
  @Transactional(readOnly = true)
  public List<MessageTemplateResponse> list() {
    return repository.findAllByOrderByChannelAscCodeAsc().stream()
        .map(MessageTemplateResponse::from)
        .toList();
  }

  /**
   * Creates a template.
   *
   * @param request template fields
   * @return saved template
   */
  @Transactional
  public MessageTemplateResponse create(SaveMessageTemplateRequest request) {
    if (request.code() == null || !CODE.matcher(request.code()).matches()) {
      throw new BusinessException(
          ErrorCode.VALIDATION_FAILED, "code must contain uppercase letters, digits, or underscores");
    }
    if (request.channel() == null) {
      throw new BusinessException(ErrorCode.VALIDATION_FAILED, "channel is required");
    }
    if (repository.existsByCodeAndChannel(request.code(), request.channel())) {
      throw new BusinessException(
          ErrorCode.BUSINESS_RULE_VIOLATION, "A template already exists for this code and channel");
    }
    validateBodies(request.channel(), request);
    parseSample(request.sampleData());
    MessageTemplate template =
        new MessageTemplate(
            request.code(), request.channel(), request.name(), request.textBody(), request.sampleData());
    apply(template, request);
    return MessageTemplateResponse.from(repository.save(template));
  }

  /**
   * Updates a template design.
   *
   * @param id template id
   * @param request replacement fields
   * @return saved template
   */
  @Transactional
  public MessageTemplateResponse update(UUID id, SaveMessageTemplateRequest request) {
    MessageTemplate template = required(id);
    if (request.version() == null || template.getVersion() != request.version()) {
      throw new BusinessException(ErrorCode.VERSION_CONFLICT, "Template was updated elsewhere");
    }
    validateBodies(template.getChannel(), request);
    parseSample(request.sampleData());
    apply(template, request);
    return MessageTemplateResponse.from(repository.save(template));
  }

  /**
   * Renders a template with sample data for the designer.
   *
   * @param id template id
   * @return rendered design
   */
  @Transactional(readOnly = true)
  public TemplatePreviewResponse preview(UUID id) {
    MessageTemplate template = required(id);
    RenderedNotification rendered =
        renderer.render(template, "preview@example.com", parseSample(template.getSampleData()));
    return new TemplatePreviewResponse(
        rendered.subject(), rendered.htmlBody(), rendered.textBody());
  }

  private MessageTemplate required(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Template was not found"));
  }

  private void apply(MessageTemplate template, SaveMessageTemplateRequest request) {
    template.setName(request.name());
    template.setDescription(request.description());
    template.setSubject(request.subject());
    template.setHtmlBody(request.htmlBody());
    template.setTextBody(request.textBody());
    template.setSampleData(request.sampleData());
    template.setEnabled(Boolean.TRUE.equals(request.enabled()));
  }

  private static void validateBodies(NotificationChannel channel, SaveMessageTemplateRequest request) {
    if (channel == NotificationChannel.EMAIL) {
      if (request.subject() == null || request.subject().isBlank()) {
        throw new BusinessException(ErrorCode.VALIDATION_FAILED, "subject is required for email");
      }
      if (request.htmlBody() == null || request.htmlBody().isBlank()) {
        throw new BusinessException(ErrorCode.VALIDATION_FAILED, "htmlBody is required for email");
      }
    }
  }

  private Map<String, String> parseSample(String json) {
    try {
      Map<String, Object> raw =
          objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
      Map<String, String> values = new LinkedHashMap<>();
      for (Map.Entry<String, Object> entry : raw.entrySet()) {
        values.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
      }
      return values;
    } catch (JsonProcessingException exception) {
      throw new BusinessException(ErrorCode.VALIDATION_FAILED, "sampleData must be a JSON object");
    }
  }
}
