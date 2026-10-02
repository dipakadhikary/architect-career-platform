# Prompt Management

## File-based templates

Knowledge prompts live under `prompts/knowledge/` (setting `prompts_root`).  
Agentic prompts live under `prompts/agentic/` (`agentic_prompts_root`).

Layout per prompt name/version:

```text
prompts/knowledge/summarize/v1/
  system.txt
  user.txt
  metadata.json
```

Examples present: knowledge `summarize/v1`; agentic `chat/v1`, `quiz/v1`, `resume/v1`.

## Rendering

- Knowledge: `FilePromptBuilder` — `string.Template.safe_substitute` for variables; optional few-shot blocks.
- Agentic: `FilePromptRegistry` — render by name/version for workflows.

Version selection: explicit version or latest folder under the prompt name.

## Governance

`PromptGovernanceService` (via `PromptGovernancePort`) supports approve / deprecate / rollback / audit with statuses such as DRAFT, APPROVED, DEPRECATED, ROLLED_BACK.

- Bootstrap (`build_prompt_governance`) auto-**approves** the latest of `chat`, `quiz`, and `resume` for local readiness.
- `prompt_require_approved` (default **false**) can require an approved prompt before pipeline resolve when enabled.
- Pipeline records `prompt_version` on evaluation/audit paths.

## Lifecycle

1. Author `system.txt` / `user.txt` / `metadata.json` in repo.
2. Reference version from settings (e.g. `summarize_prompt_version=v1`) or workflow code.
3. Pipeline records `prompt_version` on evaluation/audit paths.
4. Change prompts via new version folder — avoid silent overwrite of `v1` in production practice.

## Future A/B testing

Not implemented as a traffic splitter. Future: weighted version assignment, LangFuse experiments, per-tenant overrides.

## Interview Discussion

### Why this architecture?

Prompts as versioned files give reviewable diffs in Git and clear rollback — better than buried string constants.

### Alternative approaches

Prompts only in LangFuse UI; DB-stored prompts. Files-first keeps CI reproducible.

### Trade-offs

No runtime CMS; deploys required for prompt changes (acceptable for governed enterprise).

### Scaling considerations

Cache rendered templates; validate required variables at startup.

### How would this evolve?

Approval workflow UI; A/B; automatic regression eval on prompt PRs.

### Principal AI Architect interview questions

**Q1. How do you roll back a bad prompt?**  
Point settings/workflows back to previous version folder.

**Q2. Are prompts in the Business Platform?**  
No — AI Platform owns prompt files; Business sends content/queries only.
