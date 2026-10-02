# How to consume the SDK

## TypeScript / React

Install from the local package (until a registry publish is enabled):

```bash
npm install ../architect-career-api-sdk/typescript
```

Or link during development:

```bash
cd architect-career-api-sdk/typescript
npm run build
npm link

cd ../../architect-career-web
npm link @acos/api-sdk
```

Usage:

```ts
import { AcosApiClient } from '@acos/api-sdk';

const api = new AcosApiClient({
  basePath: import.meta.env.VITE_API_BASE_URL,
  accessToken: async () => tokenService.getAccessToken() ?? '',
  retry: { retries: 3, delayMs: 250 },
});

const page = await api.knowledge.listNotes({ page: 0, size: 20 });
```

Raw generated clients remain available when needed:

```ts
import { KnowledgeApi, Configuration } from '@acos/api-sdk/generated';
```

## Java

Add the reactor modules to your Maven build (local install first):

```bash
mvn -f java/pom.xml install
```

```xml
<dependency>
  <groupId>com.acos</groupId>
  <artifactId>architect-career-api-sdk-custom</artifactId>
  <version>0.0.1</version>
</dependency>
```

Usage:

```java
AcosApiClient client = new AcosApiClient("http://localhost:8080")
    .withBearerToken(accessToken);

client.knowledge().listNotes(0, 20, List.of("updatedAt,desc"));
```

For Feign consumers, generate with `ACOS_JAVA_CLIENT_LIBRARY=feign` and depend on the Feign artifact output under `java/generated-feign`.

## Integration tests

Prefer the custom wrappers so retries and logging behave consistently with applications. Point `basePath` at testcontainers or an ephemeral environment.

## CLI / mobile (future)

Treat this repository as the contract publisher. Generate additional language targets (Kotlin, Swift, Python) by adding OpenAPI Generator profiles without changing the OpenAPI source of truth.
