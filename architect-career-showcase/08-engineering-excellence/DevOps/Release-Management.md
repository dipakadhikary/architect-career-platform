# Release Management

## Implemented today

| Repo | Version signal | Release mechanism |
| --- | --- | --- |
| Business | Maven `0.0.1` | Manual local package / run |
| Web | npm `0.1.0` (`VITE_APP_VERSION`) | Manual `npm run build` |
| AI Platform | pyproject package version | Manual image build / uvicorn |
| AI Contracts | `VERSION` / pom / package **1.0.0** + CHANGELOG | Merge to main → CI artifacts; no registry release |

There is **no** formal release train, changelog automation for all repos, or promotion across environments.

## Contracts release posture

1. SemVer bump in `VERSION` + CHANGELOG  
2. PR must pass `mvn verify`  
3. Merge uploads SDK artifacts  
4. Consumers sync manually (AI: `sync_contracts.py`)

## Future Roadmap

- GitHub Releases / tags per repo  
- Enable GitHub Packages deploy for contracts  
- Environment promotion (dev → stage → prod)  
- Signed images and SBOMs


## Interview Discussion

### Why this approach?

Portfolio stage prioritizes correct architecture over release automation.

### Alternative approaches

CalVer; trunk-based continuous deploy. Premature without envs.

### Trade-offs

Manual sync risk between contracts and AI vendoring.

### Enterprise adoption

Adopt GitOps release once Docker/K8s targets exist.

### Scaling considerations

Semantic-release + required consumer version pins.

### Principal Architect interview questions

**Q1. Is there a production CD pipeline?**  
No — not implemented.

**Q2. How do contracts reach AI Platform?**  
Generate then vendor via `third_party/acos_ai_contracts`.
