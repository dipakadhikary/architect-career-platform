# Jakarta Bean Validation

## Introduction

Declarative constraints (`@NotNull`, `@Size`, …) validated at controller/service boundaries.

## Problem Statement

Ad-hoc `if` checks duplicate and miss nested objects.

## Why ACOS Uses This

ACOS validates API inputs and password policy; generated Java AI models also enable Bean Validation annotations from OpenAPI.

## Implementation Overview

Use `@Valid` on request bodies; map violations to API error envelopes / Problem Details style.

## Best Practices

- Validate at the edge and trust less inward.
- Align OpenAPI constraints with annotations when generating.

## Common Mistakes

- Only validating in UI (Web Zod) and skipping server checks.
- Weak password rules.

## Alternative Approaches

Manual validators; JSON Schema only; TypeScript-only checks.

## Trade-offs

Standard annotations vs limited expressiveness for cross-field rules (custom validators needed).

## References to ACOS modules

- PasswordValidator / controller validation in Business
- Contracts Java generator `useBeanValidation`

## Interview Questions

**Q:** Is Web Zod enough?
**A:** No—server must revalidate.

**Q:** Cross-field rules?
**A:** Class-level custom constraints.

## Further Reading

- Jakarta Bean Validation spec

