# Forms and Validation

## Validation strategy

| Layer | Tool |
| --- | --- |
| Schema | Zod per feature (`schemas/`) |
| Form state | React Hook Form |
| Bridge | `zodResolver` from `@hookform/resolvers` |

Used across auth, knowledge, learning, portfolio, career dialogs, and AI capability forms.

## Error presentation

RHF `formState.errors` mapped to MUI `TextField` `error`/`helperText` (and Controller wrappers). API errors after submit surface via notification store or inline alerts.

## Reusable form components

- `FormDialog` shared shell for modal forms
- Feature-specific `*FormDialog` / `*Form` composing fields
- AI pages embed multiple forms (resume/interview/cover letter, etc.)

## Submission flow

1. Client Zod validation blocks invalid submit
2. `handleSubmit` → feature mutation hook → Axios API
3. On success: close dialog, invalidate queries, toast
4. On failure: map `ApiClientError` to user message

## Interview Discussion

### Why this architecture?

Zod schemas can mirror Business validation rules and be reused outside React; RHF minimizes re-renders.

### Alternative approaches

Formik; MUI X; server-only validation. Client schemas improve UX.

### Trade-offs

Schema duplication vs backend annotations — keep manually aligned.

### Scaling considerations

Share Zod types with generated OpenAPI later; standard error mapper.

### How would this evolve?

Draft autosave; multi-step wizards for career applications.

### Common Frontend Architect interview questions

**Q1. Where is login schema?**  
Auth feature/page Zod schema + `zodResolver`.

**Q2. Do forms call AI Platform?**  
No — mutations hit Business paths (AI BFF when present).
