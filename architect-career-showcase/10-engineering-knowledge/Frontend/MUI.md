# MUI (Material UI)

## Introduction

MUI provides accessible React components and theming primitives.

## Problem Statement

Building every Accordion/Dialog from scratch delays product UX.

## Why ACOS Uses This

ACOS Web uses MUI 7 for layout, inputs, and feedback patterns consistent with an enterprise tool aesthetic.

## Implementation Overview

ACOS Web uses MUI 7 for layout, inputs, and feedback patterns consistent with an enterprise tool aesthetic.

## Best Practices

Centralize theme tokens; prefer composition over one-off styles; keep a11y props (`aria-*`) on custom wrappers.

## Common Mistakes

- Overriding everything until MUI benefits disappear.
- Ignoring keyboard semantics on custom tables.

## Alternative Approaches

Chakra; Ant Design; pure CSS modules.

## Trade-offs

Speed vs bundle weight (mitigated via manualChunks `mui`).

## References to ACOS modules

- [05-web-platform/UI-Design-System.md](../../05-web-platform/UI-Design-System.md)

## Interview Questions

**Q:** Why chunk MUI separately?
**A:** Cacheable vendor split for repeat visits.

**Q:** Design system ownership?
**A:** Shared components under Web `shared/`.

## Further Reading

- mui.com

