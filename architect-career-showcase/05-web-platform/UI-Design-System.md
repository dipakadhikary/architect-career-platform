# UI Design System

## Foundation

MUI 7 + Emotion with `createAppTheme(mode)` in `shared/theme/theme.ts`. Mode from Zustand theme store (**persisted** light/dark); `ThemeProvider` applies `CssBaseline`.

## Typography

- Primary: **DM Sans** (UI)
- Mono / overline: **IBM Plex Mono**
- Buttons: `textTransform: 'none'`, weight 600
- Headings: tightened letter-spacing

## Color system

Custom light/dark palettes (`palette.ts`) with brand greens/golds aligned to PWA manifest (`theme_color: #0B3D2E`, `background_color: #F3F6F4`). Body uses subtle radial gradients (not flat white).

## Spacing & shape

MUI spacing scale; global `shape.borderRadius: 10`; buttons radius 8. Drawer widths from `appConfig.layout` (260 / 72 collapsed).

## Responsive design

AppLayout + MUI Grid/Stack breakpoints; drawer collapses for smaller viewports via layout components. Feature pages use responsive stacks/tables.

## Reusable components

Shared kit listed in Component Architecture; AI-specific cards for capability UX.

## Accessibility considerations

Theme contrast aimed at readable enterprise UI; see Accessibility doc for ARIA usage. Prefer MUI primitives (which ship a11y attributes) over raw divs.

## Interview Discussion

### Why this architecture?

MUI accelerates enterprise forms/tables; custom theme avoids generic purple defaults and matches ACOS brand.

### Alternative approaches

Chakra; fully custom CSS; MUI unstyled + Tailwind. MUI fits dense data UIs.

### Trade-offs

Bundle weight — mitigated by manualChunks (`mui` chunk).

### Scaling considerations

Design tokens package; dark mode persistence already via store; theme density options.

### How would this evolve?

Storybook token docs; tighter WCAG audits.

### Common Frontend Architect interview questions

**Q1. How is dark mode toggled?**  
Zustand theme store → `createAppTheme`.

**Q2. Why DM Sans?**  
Distinctive, readable UI font vs default system stacks.
