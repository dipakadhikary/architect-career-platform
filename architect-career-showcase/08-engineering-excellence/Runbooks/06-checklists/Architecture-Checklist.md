# Architecture Review Checklist

## Purpose

Operationalize architecture review for changes that cross repository or trust boundaries.

## Scope

PRs affecting contracts, AI integration, security, or multi-repo deployment topology. Complements ADRs in chapter 03.

## Audience

- Architect
- Platform Engineer
- Principal Engineer
- SRE (for operability)

## Preconditions

- Linked ADR or design note for cross-cutting changes
- Runbooks/CI impact considered

## Step-by-Step Procedure (Checklist)

- [ ] Trust boundary preserved (Web → Business only; no browser→AI)
- [ ] AI changes go through contracts SemVer when HTTP/events change
- [ ] Anti-corruption layer retained (no domain Feign leakage)
- [ ] Feature flag / degrade path documented for new AI features
- [ ] Health/readiness impact reviewed
- [ ] Observability: correlation ID on new I/O
- [ ] Security checklist touched if auth/secrets change
- [ ] Operability: runbook update if new failure mode
- [ ] Future enhancements not marketed as implemented
- [ ] ADRs updated when decision changes

## Validation

Architect sign-off recorded on PR or CAB note.

## Rollback

Block merge until checklist satisfied or explicitly waived.

## Troubleshooting

Disputes → cite chapter 03 ADRs and chapter 08 Architecture Governance.

## Escalation

Unresolved boundary violations → Engineering leadership.

## References

- [../../Governance/Architecture-Governance.md](../../Governance/Architecture-Governance.md)
- [../02-operations/Release-Checklist.md](../02-operations/Release-Checklist.md)
- Chapter 03 ADR index
