# Suggested Screenshots

Capture these for portfolio README / presentations (store assets outside git or in an approved media folder):

1. Web login / register  
2. Authenticated home / sidebar navigation  
3. Knowledge note list + editor  
4. Learning plan with milestones  
5. Career pipeline board/list  
6. Portfolio projects  
7. Business Swagger UI (optional)  
8. Actuator health JSON (readiness)  
9. AI `/api/v1/system/metrics` Prometheus text (optional)  
10. Contracts `openapi/ai-platform-v1.yaml` tree in IDE  
11. GitHub Actions green check on contracts/AI (if available)  
12. Architecture Mermaid from showcase README  

**Note:** Do not screenshot secrets, `.env` values, or API keys.


## Interview Discussion

### Why this approach?

Visual proof beats claims.

### Alternative approaches

Stock dashboards that aren't yours. Risky.

### Trade-offs

Screenshots drift — date them.

### Enterprise adoption

Use sanitized demo tenant data only.

### Scaling considerations

Automate Playwright screenshots in CI.

### Principal Architect interview questions

**Q1. Any secret in shots?**  
Never.

**Q2. Best architecture visual?**  
Four-repo trust-boundary diagram.
