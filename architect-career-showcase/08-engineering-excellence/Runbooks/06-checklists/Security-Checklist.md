# Security Checklist

## Purpose

Security review items grounded in ACOS controls and known gaps.

## Scope

Authn/z, secrets, AI safety, dependency scanning.

## Audience

- Architect
- DevSecOps
- Platform Engineer
- SRE

## Preconditions

- Access to configs and CI scan outputs

## Step-by-Step Procedure (Checklist)

- [ ] JWT secret strong; not default in shared env
- [ ] Refresh tokens hashed; logout revoke verified
- [ ] Password policy enforced server-side
- [ ] AI auth mode on for any non-local shared env
- [ ] Provider keys only on AI; never in Web `VITE_*`
- [ ] Guardrails enabled for exposed AI routes
- [ ] Rate limit considered for shared demos
- [ ] `rehype-sanitize` retained for markdown
- [ ] Dependency scans reviewed (pip-audit/bandit/Trivy — soft today)
- [ ] No `.env` committed
- [ ] CSP/security headers plan noted (**gap** / Future)
- [ ] localStorage XSS residual risk accepted or cookie migration planned

## Validation

Findings tracked with severity and owners.

## Rollback

Block release on Critical unresolved findings.

## Troubleshooting

Open AI auth (`all flags false`) is a Critical in shared envs.

## Escalation

Suspected leak → immediate [../02-operations/Secret-Rotation.md](../02-operations/Secret-Rotation.md).

## References

- [../../Security/](../../Security/)
- Threat Model chapter 08
