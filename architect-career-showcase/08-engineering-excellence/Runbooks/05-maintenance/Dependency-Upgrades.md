# Dependency Upgrades

## Purpose

Upgrade libraries safely across Maven, npm, and Python ecosystems used by ACOS.

## Scope

Business (`pom.xml`), Web (`package.json`), AI (`pyproject.toml`), Contracts (OpenAPI Generator plugin / Node validators). Not OS/runtime upgrades on cloud nodes (**Future enhancement**).

## Audience

- Developer
- Platform Engineer
- DevOps Engineer
- Architect

## Preconditions

- Clean git workspace
- Ability to run full local test gates
- Docker for Testcontainers / compose where needed

## Step-by-Step Procedure

1. **Pick one repo per PR** — avoid multi-repo dependency bombs.
2. **Business:** bump versions in `pom.xml`; run `mvn verify`.
3. **Web:** bump with npm; run `npm run lint && npm test && npm run build` (+ e2e if UI-sensitive).
4. **AI:** bump `pyproject.toml`; run ruff/black/mypy/pytest; re-run `pip-audit`/`bandit`; rebuild image + Trivy if Dockerfile affected.
5. **Contracts:** bump generator carefully; `mvn clean verify`; sync AI Python package; smoke imports.
6. **Review changelogs** for Spring Boot / React / Pydantic majors.
7. **Commit separately** from feature work.

## Validation

All repo gates green; smoke auth+CRUD; AI contract import check passes.

## Rollback

Revert the dependency PR; redeploy previous artifact.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Spotless flood | Format or restore ratchet strategy |
| Peer dependency npm | Resolve with overrides carefully |
| Pydantic breaks | Pin and read migration guide |

## Escalation

Escalate Spring Boot major upgrades to Architect for security filter / Actuator review.

## References

- [../../DevOps/Build-Pipeline.md](../../DevOps/Build-Pipeline.md)
- AI CI workflow supply-chain steps
