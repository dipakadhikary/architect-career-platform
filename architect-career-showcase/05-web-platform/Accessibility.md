# Accessibility

## Implemented practices

- Skip link in `AppLayout` → `#main-content` (main landmark)
- MUI components as baseline (keyboard-focusable controls, dialog patterns)
- Explicit `aria-label` / `aria-labelledby` / `aria-describedby` on dialogs, icon buttons, tables, chat input, loaders
- `aria-live="polite"` and `aria-busy` on loading indicators
- Sidebar `aria-label="Primary"`; table column `scope="col"` where used
- Markdown links use `rel="noopener noreferrer"`; content sanitized (`rehype-sanitize`)

## Keyboard navigation

Rely on MUI Dialog/Menu/Drawer focus traps and native tab order. Custom widgets should keep IconButtons labeled.

## Screen reader compatibility

Status chips/banners (AI unavailable, offline) should remain text-visible, not color-only. Loading regions announce busy state where marked.

## Color contrast

Theme palettes targeting readable enterprise contrast in light/dark; verify with audits when changing brand colors.

## Responsive behaviour

Drawer collapse and stacked layouts; touch targets from MUI defaults.

## Gaps (honest)

No full axe CI gate documented in package scripts; a11y is by practice, not automated enforcement yet.

## Interview Discussion

### Why this architecture?

Prefer MUI primitives + intentional ARIA on icon-only controls over bespoke widgets.

### Alternative approaches

aria-query lint in CI; Playwright a11y snapshots. Good next steps.

### Trade-offs

Custom AI chat UIs need ongoing a11y review.

### Scaling considerations

Add eslint-plugin-jsx-a11y; chromatic/axe in PR.

### How would this evolve?

WCAG 2.2 AA checklist per feature; expand automated axe coverage beyond the existing skip link + labeled controls.

### Common Frontend Architect interview questions

**Q1. How are icon-only buttons handled?**  
`aria-label` on IconButton (delete, copy, etc.).

**Q2. Is markdown safe?**  
Sanitized via rehype-sanitize before highlight/render.
