# Release Readiness Checklist

## Purpose

Operational go/no-go before merging or cutting a demo/release.

## Scope

Complements [../02-operations/Release-Checklist.md](../02-operations/Release-Checklist.md) with broader sign-off.

## Audience

- Developer
- DevOps Engineer
- Architect
- SRE

## Preconditions

- Feature complete on branch
- Access to CI where available

## Step-by-Step Procedure (Checklist)

- [ ] Contracts SemVer + CHANGELOG (if applicable)
- [ ] AI CI green; Business `mvn verify`; Web lint/test/build
- [ ] Migrations reviewed (expand/contract)
- [ ] Feature flags default safe
- [ ] Smoke auth + CRUD (+ AI if advertised)
- [ ] No secrets in git diff
- [ ] Runbooks updated if ops changed
- [ ] Known limitations listed for stakeholders
- [ ] Rollback owner named

## Validation

All boxes checked or waived.

## Rollback

Block release; see [../02-operations/Rollback.md](../02-operations/Rollback.md).

## Troubleshooting

Soft-fail scanners: document risk acceptance explicitly.

## Escalation

MAJOR API changes → Architect.

## References

- Chapter 07 Versioning
- Chapter 08 Governance Review Checklist
