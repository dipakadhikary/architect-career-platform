package com.acos.notification.dispatch;

import com.acos.notification.adapter.ConsoleNotificationAdapter;
import com.acos.notification.api.NotificationMessage;
import com.acos.notification.api.NotificationPort;
import com.acos.notification.api.RenderedNotification;
import com.acos.notification.config.NotificationProperties;
import com.acos.notification.template.MessageTemplate;
import com.acos.notification.template.MessageTemplateRepository;
import com.acos.notification.template.TemplateRenderer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Selects a channel adapter, renders the database template, and falls back to the console when the
 * provider cannot send.
 */
@Service
public class NotificationDispatcher implements NotificationPort {

  private static final Logger LOG = LoggerFactory.getLogger(NotificationDispatcher.class);

  private final MessageTemplateRepository templateRepository;
  private final TemplateRenderer renderer;
  private final NotificationProperties properties;
  private final List<ChannelAdapter> adapters;
  private final ConsoleNotificationAdapter console;

  /**
   * Creates the dispatcher.
   *
   * @param templateRepository template store
   * @param renderer placeholder renderer
   * @param properties provider selection
   * @param adapters registered providers
   * @param console fallback delivery
   */
  public NotificationDispatcher(
      MessageTemplateRepository templateRepository,
      TemplateRenderer renderer,
      NotificationProperties properties,
      List<ChannelAdapter> adapters,
      ConsoleNotificationAdapter console) {
    this.templateRepository = templateRepository;
    this.renderer = renderer;
    this.properties = properties;
    this.adapters = adapters;
    this.console = console;
  }

  @Override
  public void deliver(NotificationMessage message) {
    String provider = properties.providerFor(message.channel());
    Optional<MessageTemplate> template =
        templateRepository.findByCodeAndChannel(message.templateCode(), message.channel());
    RenderedNotification rendered =
        template
            .filter(MessageTemplate::isEnabled)
            .map(found -> renderer.render(found, message.recipient(), message.variables()))
            .orElseGet(() -> plain(message));
    if (template.isEmpty() || !template.get().isEnabled()) {
      LOG.warn(
          "Notification template {} {} is not active; using console delivery",
          message.channel(),
          message.templateCode());
      console.deliver(rendered);
      return;
    }
    ChannelAdapter adapter =
        adapters.stream()
            .filter(candidate -> candidate.supports(message.channel(), provider))
            .findFirst()
            .orElse(console);
    if (adapter == console) {
      console.deliver(rendered);
      return;
    }
    if (!adapter.available()) {
      LOG.info(
          "Notification provider {} is not available for {}", provider, message.templateCode());
      console.deliver(rendered);
      return;
    }
    try {
      adapter.deliver(rendered);
      LOG.info(
          "Notification {} sent with provider {}", message.templateCode(), provider);
    } catch (RuntimeException exception) {
      LOG.warn(
          "Notification provider {} failed for template {}: {}",
          provider,
          message.templateCode(),
          exception.getClass().getSimpleName());
      console.deliver(rendered);
    }
  }

  private static RenderedNotification plain(NotificationMessage message) {
    String text =
        message.variables() == null
            ? ""
            : message.variables().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + value(entry.getValue()))
                .collect(Collectors.joining("\n"));
    return new RenderedNotification(
        message.channel(), message.recipient(), message.templateCode(), "", "", text);
  }

  private static String value(String raw) {
    return raw == null ? "" : raw;
  }
}
