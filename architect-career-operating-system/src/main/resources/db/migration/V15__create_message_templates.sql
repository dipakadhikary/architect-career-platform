-- Channel templates for the notification module. Email and SMS share this table.

CREATE TABLE acos.message_templates (
    id            UUID                     NOT NULL,
    code          VARCHAR(64)              NOT NULL,
    channel       VARCHAR(16)              NOT NULL,
    name          VARCHAR(120)             NOT NULL,
    description   VARCHAR(500),
    subject       VARCHAR(200),
    html_body     TEXT,
    text_body     TEXT                     NOT NULL,
    sample_data   TEXT                     NOT NULL,
    enabled       BOOLEAN                  NOT NULL,
    created_at    TIMESTAMPTZ              NOT NULL,
    updated_at    TIMESTAMPTZ              NOT NULL,
    version       BIGINT                   NOT NULL,
    CONSTRAINT pk_message_templates PRIMARY KEY (id),
    CONSTRAINT uk_message_templates_code_channel UNIQUE (code, channel),
    CONSTRAINT ck_message_templates_channel CHECK (channel IN ('EMAIL', 'SMS'))
);

COMMENT ON TABLE acos.message_templates IS
    'Editable email and SMS templates. Placeholders use {{variable}} syntax.';

INSERT INTO acos.message_templates (
    id, code, channel, name, description, subject, html_body, text_body, sample_data,
    enabled, created_at, updated_at, version
) VALUES (
    gen_random_uuid(),
    'LOGIN_IDENTIFIER',
    'EMAIL',
    'Login identifier',
    'Sent when someone asks ACOS for the email they use to sign in.',
    'ACOS - Your login identifier',
    $html$<!DOCTYPE html>
<html>
<body style="margin:0;background:#f4f7f5;font-family:Georgia,serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
    <tr><td align="center" style="padding:32px 16px;">
      <table role="presentation" width="560" cellpadding="0" cellspacing="0"
             style="background:#ffffff;border:1px solid #d7e3dc;">
        <tr><td style="background:#0B3D2E;padding:24px 32px;color:#E8F0EC;">
          <div style="font-size:12px;letter-spacing:0.14em;color:#C4A35A;">ARCHITECT CAREER OS</div>
          <div style="font-size:22px;margin-top:8px;">Your login identifier</div>
        </td></tr>
        <tr><td style="padding:32px;color:#1c2b24;font-size:16px;line-height:1.5;">
          <p>You requested the identifier you use to sign in to ACOS.</p>
          <p style="font-size:18px;"><strong>{{loginIdentifier}}</strong></p>
          <p style="font-size:13px;color:#5c6b64;">If you did not request this, you can ignore this email.</p>
          <p>Regards,<br>ACOS Team</p>
        </td></tr>
      </table>
    </td></tr>
  </table>
</body>
</html>$html$,
    $text$You requested the identifier you use to sign in to ACOS.

Login identifier: {{loginIdentifier}}

If you did not request this, you can ignore this email.

Regards,
ACOS Team$text$,
    '{"loginIdentifier":"ada@acos.local"}',
    TRUE,
    NOW(),
    NOW(),
    0
);

INSERT INTO acos.message_templates (
    id, code, channel, name, description, subject, html_body, text_body, sample_data,
    enabled, created_at, updated_at, version
) VALUES (
    gen_random_uuid(),
    'PASSWORD_RESET',
    'EMAIL',
    'Password reset',
    'Sent when someone asks ACOS to reset a password.',
    'ACOS - Reset your password',
    $html$<!DOCTYPE html>
<html>
<body style="margin:0;background:#f4f7f5;font-family:Georgia,serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
    <tr><td align="center" style="padding:32px 16px;">
      <table role="presentation" width="560" cellpadding="0" cellspacing="0"
             style="background:#ffffff;border:1px solid #d7e3dc;">
        <tr><td style="background:#0B3D2E;padding:24px 32px;color:#E8F0EC;">
          <div style="font-size:12px;letter-spacing:0.14em;color:#C4A35A;">ARCHITECT CAREER OS</div>
          <div style="font-size:22px;margin-top:8px;">Reset your password</div>
        </td></tr>
        <tr><td style="padding:32px;color:#1c2b24;font-size:16px;line-height:1.5;">
          <p>You requested a password reset for your ACOS account.</p>
          <p><a href="{{resetUrl}}"
                style="display:inline-block;background:#0B3D2E;color:#ffffff;text-decoration:none;padding:12px 20px;">
                Reset password</a></p>
          <p style="font-size:13px;color:#5c6b64;">If you did not request this, you can ignore this email.</p>
          <p>Regards,<br>ACOS Team</p>
        </td></tr>
      </table>
    </td></tr>
  </table>
</body>
</html>$html$,
    $text$You requested a password reset for your ACOS account.

Reset your password:
{{resetUrl}}

If you did not request this, you can ignore this email.

Regards,
ACOS Team$text$,
    '{"resetUrl":"http://localhost:5173/reset-password?token=sample"}',
    TRUE,
    NOW(),
    NOW(),
    0
);

INSERT INTO acos.message_templates (
    id, code, channel, name, description, subject, html_body, text_body, sample_data,
    enabled, created_at, updated_at, version
) VALUES (
    gen_random_uuid(),
    'PASSWORD_RESET',
    'SMS',
    'Password reset SMS',
    'Text-message form of the password reset notice. Delivery uses the SMS adapter.',
    NULL,
    NULL,
    $text$ACOS password reset: {{resetUrl}}$text$,
    '{"resetUrl":"http://localhost:5173/reset-password?token=sample"}',
    TRUE,
    NOW(),
    NOW(),
    0
);
