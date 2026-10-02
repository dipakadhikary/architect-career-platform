# Release Checklist

## Purpose

Gate a release or demo cut on evidence from implemented quality tools.

## Scope

Pre-merge to `main` and pre-demo. Not a formal production CAB process (**Future enhancement**).

## Audience

- Developer
- DevOps Engineer
- Architect

## Preconditions

- Access to repo CI where present (AI, Contracts)
- Ability to run local gates for Business/Web

## Step-by-Step Procedure

### Checklist

- [ ] **Contracts** (if AI API touched): PR green on workflow `Validate Contracts and Generate`; CHANGELOG/VERSION SemVer correct
- [ ] **AI Platform**: workflow `AI Platform CI` green; contracts synced; no accidental auth-open in shared envs
- [ ] **Business**: `mvn verify` locally (no GitHub Actions yet — **gap**)
- [ ] **Web**: `npm run lint && npm test && npm run build` (no GitHub Actions yet — **gap**)
- [ ] Feature flags default safe (`AI_PLATFORM_ENABLED=false` unless demo needs AI)
- [ ] Secrets not committed; JWT secret not default in shared env
- [ ] Smoke: auth + one CRUD; AI path only if enabled
- [ ] Runbooks/README updated if ops steps changed
- [ ] Known gaps communicated (AsyncAPI broker, BFF gaps, etc.)

## Validation

All checked items complete or explicitly waived with owner name.

## Rollback

Do not merge/release; return to development; see [Rollback.md](Rollback.md) if already partially deployed.

## Troubleshooting

If CI is red only on soft-fail scanners (mypy/bandit/Trivy), document risk acceptance — do not ignore silently.

## Escalation

Escalate to Architect for MAJOR contracts SemVer or security-sensitive releases.

## References

- [../06-checklists/Release-Readiness.md](../06-checklists/Release-Readiness.md)
- Chapters 07 & 08 DevOps docs
