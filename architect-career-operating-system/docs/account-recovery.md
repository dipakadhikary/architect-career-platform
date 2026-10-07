# Account recovery

Login uses the account email. There is no separate username. Forgot User ID emails that email
address. The HTTP response never contains it.

## Flows

### Forgot User ID

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant AuthAPI
    participant UserStore
    participant EmailService

    User->>UI: Enter email
    UI->>AuthAPI: POST /api/v1/auth/forgot-user-id
    AuthAPI->>UserStore: Find enabled user
    alt Account exists
        AuthAPI->>EmailService: Email the login email
    end
    AuthAPI-->>UI: Generic acknowledgement
```

### Forgot Password

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant AuthAPI
    participant TokenStore
    participant EmailService

    User->>UI: Enter email
    UI->>AuthAPI: POST /api/v1/auth/forgot-password
    alt Enabled account exists
        AuthAPI->>TokenStore: Supersede unused tokens and store SHA-256
        AuthAPI->>EmailService: Email link with the raw token
    end
    AuthAPI-->>UI: Generic acknowledgement
```

### Reset Password

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant AuthAPI
    participant TokenStore
    participant UserStore

    User->>UI: Open /reset-password?token=...
    UI->>AuthAPI: POST /api/v1/auth/reset-password
    AuthAPI->>TokenStore: Lock row by token hash
    AuthAPI->>TokenStore: Consume token
    AuthAPI->>UserStore: Store new BCrypt hash
    AuthAPI->>UserStore: Revoke refresh tokens
    AuthAPI-->>UI: Success
    UI-->>User: Login
```

### Invalid reset token

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant AuthAPI
    participant TokenStore

    User->>UI: Submit new password
    UI->>AuthAPI: POST /api/v1/auth/reset-password
    AuthAPI->>TokenStore: Hash token and lock row
    TokenStore-->>AuthAPI: Missing, expired, or already used
    AuthAPI-->>UI: This password reset link is invalid or has expired.
```

### Concurrent reset

```mermaid
sequenceDiagram
    participant RequestA
    participant RequestB
    participant TokenStore

    RequestA->>TokenStore: Lock token row
    RequestB->>TokenStore: Wait for lock
    TokenStore-->>RequestA: Unused token
    RequestA->>TokenStore: Mark used and change password
    TokenStore-->>RequestB: Already used
    RequestB-->>RequestB: Reject
```

## Token lifecycle

A new reset request marks unused tokens for that user as used, then stores a new SHA-256 hash.
The raw token is 32 random bytes, Base64 URL encoded, and is only placed in the email link.
The link origin comes from `acos.recovery.frontend-base-url`. The request cannot choose the host.

Default lifetime is 30 minutes (`acos.recovery.token-ttl`). Expiry is checked when the token is
consumed. There is no scheduler; `deleteByUsedAtIsNullAndExpiresAtBefore` is available for storage
hygiene only.

## Email

The platform has no SMTP integration. `LoggingAccountMailSender` records that a message was
accepted and does not log the address, identifier, or reset URL. A mail failure still returns the
generic acknowledgement. The token row remains so a later delivery mechanism can be added without
changing the public contract.

## Rate limiting

Public recovery endpoints share an in-memory limiter: 5 requests per email per hour and at least
60 seconds between requests. A limited request returns the same generic message.

## Sessions

Refresh tokens for the account are revoked when a reset succeeds. Access JWTs are stateless and
remain valid until `acos.jwt.access-token-ttl` elapses.

## API

| Method | Path | Public response |
| --- | --- | --- |
| POST | `/api/v1/auth/forgot-user-id` | Generic acknowledgement |
| POST | `/api/v1/auth/forgot-password` | Generic acknowledgement |
| POST | `/api/v1/auth/reset-password` | Empty success, or invalid-token / weak-password error |
