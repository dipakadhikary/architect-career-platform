# k6 load scenario scaffold for future benchmarking.
# Run: k6 run scripts/load/k6_smoke.js

import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 10,
  duration: "30s",
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<2000"],
  },
};

const BASE = __ENV.BASE_URL || "http://127.0.0.1:8090";

export default function () {
  const health = http.get(`${BASE}/api/v1/ai/health`);
  check(health, { "health 200": (r) => r.status === 200 });

  const chat = http.post(
    `${BASE}/api/v1/ai/chat/completions`,
    JSON.stringify({
      userId: "k6-user",
      message: "What is CQRS?",
    }),
    { headers: { "Content-Type": "application/json" } }
  );
  check(chat, { "chat accepted": (r) => r.status === 200 || r.status === 422 });
  sleep(0.2);
}
