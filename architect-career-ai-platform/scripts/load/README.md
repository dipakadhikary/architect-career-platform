"""Load testing infrastructure notes and entrypoints.

- Locust: `scripts/load/locustfile.py`
- k6: `scripts/load/k6_smoke.js`

Install Locust separately when needed:
  pip install locust

Scenarios covered:
- Concurrent HTTP users
- Chat / knowledge search throughput
- Large document summarization
- Long conversation payloads

Future work: distributed Locust workers and provider-level benchmarking harnesses.
"""
