package com.acos.notification.template;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.notification.api.NotificationChannel;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Placeholder rendering for email HTML and SMS text. */
class TemplateRendererTest {

  @Test
  void shouldSubstituteValuesAndEscapeHtml() {
    MessageTemplate template =
        new MessageTemplate(
            "PASSWORD_RESET",
            NotificationChannel.EMAIL,
            "Reset",
            "Open {{resetUrl}}",
            "{}");
    template.setSubject("Reset {{resetUrl}}");
    template.setHtmlBody("<a href=\"{{resetUrl}}\">{{loginIdentifier}}</a>");

    var rendered =
        new TemplateRenderer()
            .render(
                template,
                "ada@acos.local",
                Map.of(
                    "resetUrl", "http://localhost:5173/reset-password?token=sample",
                    "loginIdentifier", "<ada@acos.local>"));

    assertThat(rendered.textBody())
        .isEqualTo("Open http://localhost:5173/reset-password?token=sample");
    assertThat(rendered.htmlBody()).contains("token=sample").contains("&lt;ada@acos.local&gt;");
    assertThat(rendered.htmlBody()).doesNotContain("<ada@acos.local>");
  }
}
