package com.acos.notification.template;

import com.acos.notification.api.RenderedNotification;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Replaces {@code {{name}}} placeholders. HTML values are escaped. */
@Component
public class TemplateRenderer {

  /**
   * Renders a template with the supplied values.
   *
   * @param template stored template
   * @param recipient destination
   * @param variables placeholder values
   * @return rendered subject and bodies
   */
  public RenderedNotification render(
      MessageTemplate template, String recipient, Map<String, String> variables) {
    Map<String, String> values = variables == null ? Map.of() : variables;
    return new RenderedNotification(
        template.getChannel(),
        recipient,
        template.getCode(),
        apply(template.getSubject(), values, true),
        apply(template.getHtmlBody(), values, true),
        apply(template.getTextBody(), values, false));
  }

  static String apply(String source, Map<String, String> variables, boolean html) {
    if (source == null || source.isEmpty()) {
      return "";
    }
    String result = source;
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      String value = entry.getValue() == null ? "" : entry.getValue();
      if (html) {
        value = escapeHtml(value);
      }
      result = result.replace("{{" + entry.getKey() + "}}", value);
    }
    return result;
  }

  private static String escapeHtml(String value) {
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
