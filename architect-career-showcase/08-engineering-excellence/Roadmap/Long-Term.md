# Long-Term Roadmap (9–24 months)

- Kubernetes deploy with HPA, ingress TLS, sealed secrets  
- OIDC enterprise IdP; httpOnly sessions; step-up auth for sensitive AI  
- Multi-tenant isolation on by default; cost budgets per tenant  
- Real MCP/A2A networked tools with allowlists  
- Analytics domain beyond stubs; live dashboards  
- SLO/error budgets + Alertmanager  
- Possible extraction of read-heavy services **if** metrics justify  
- Model risk governance and retention policies for prompts/outputs  


## Interview Discussion

### Why this approach?

Long-term connects portfolio system to enterprise operating model.

### Alternative approaches

Rewrite in greenfield. Usually waste.

### Trade-offs

Ambition must stay funded.

### Enterprise adoption

Align to org cloud standards.

### Scaling considerations

Cell-based architecture if tenant count explodes.

### Principal Architect interview questions

**Q1. When split the monolith?**  
When deploy cadence or scaling axes diverge with evidence.

**Q2. End-state AI auth?**  
mTLS + workload identity + user JWT propagation.
