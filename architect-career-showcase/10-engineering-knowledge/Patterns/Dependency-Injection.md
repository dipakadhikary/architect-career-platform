# Dependency Injection

## Introduction

DI supplies collaborators from the outside, enabling testing with fakes and swapping adapters.

## Problem Statement

`new`ing infrastructure inside domain logic freezes implementations.

## Why ACOS Uses This

Spring `@Service`/`@Component` constructor injection on Business; FastAPI/Depends + composition root style on AI for ports.

## Implementation Overview

Spring `@Service`/`@Component` constructor injection on Business; FastAPI/Depends + composition root style on AI for ports.

## Best Practices

Prefer constructor injection; avoid field injection; bind ports to adapters once.

## Common Mistakes

- Circular dependencies as design smell.
- Service locators everywhere.

## Alternative Approaches

Manual pure DI; service locator; monads.

## Trade-offs

Framework power vs magic. Standard for ACOS stacks.

## References to ACOS modules

- Business configuration packages
- AI DI wiring modules

## Interview Questions

**Q:** Why constructor injection?
**A:** Required deps explicit; easier tests.

**Q:** How AI swaps LLM providers?
**A:** Port interface + adapter binding.

## Further Reading

- Spring DI docs; FastAPI dependencies

