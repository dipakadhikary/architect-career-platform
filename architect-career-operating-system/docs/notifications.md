# Notifications

`com.acos.notification` sends email and SMS. Auth calls `NotificationPort` and does not know which
provider is configured. The package can move into a separate email and SMS service: it depends on
`com.acos.common`, Spring, and the AWS SDK, and it does not depend on the auth domain.

## Add a provider

Implement `ChannelAdapter` and register it as a Spring bean.

- `supports(EMAIL, "aws")` is Amazon SES (`AwsSesEmailAdapter`).
- `supports(channel, "console")` prints the rendered text, including a reset URL.
- A later SMS provider implements `supports(SMS, "your-provider")` and is selected with
  `ACOS_SMS_PROVIDER`.

```yaml
acos:
  notification:
    email:
      provider: aws
      from-address: ${ACOS_EMAIL_FROM:}
      region: ${ACOS_AWS_REGION:us-east-1}
    sms:
      provider: console
```

SES uses the default AWS credential chain. Leave `ACOS_EMAIL_FROM` empty until the sender address
is verified. Delivery then stays on the console.

## Templates

`acos.message_templates` stores one design per code and channel. Placeholders use `{{name}}`.
HTML substitution escapes the value. The designer at `/notifications/templates` shows the email
HTML in a sandboxed frame and the SMS text in a message preview. Sample JSON in `sample_data`
fills the preview.

Seeded codes:

| Code | Channel | Used by |
| --- | --- | --- |
| `LOGIN_IDENTIFIER` | EMAIL | Forgot user ID |
| `PASSWORD_RESET` | EMAIL | Forgot password |
| `PASSWORD_RESET` | SMS | Designer sample until an SMS adapter sends it |
