#!/usr/bin/env node
/**
 * Downloads the ACOS OpenAPI document from a running Java Business Platform.
 *
 * Preferred endpoint: GET /v3/api-docs (JSON) — SpringDoc default.
 * Fallback: GET /v3/api-docs.yaml, then GET /api-docs, then local openapi.yaml path.
 *
 * Environment:
 *   ACOS_OPENAPI_URL   Full URL to the OpenAPI document (overrides base + path)
 *   ACOS_API_BASE_URL  Backend base URL (default http://127.0.0.1:8080)
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const openapiDir = path.join(root, 'openapi');
const jsonOut = path.join(openapiDir, 'acos-api.json');

const baseUrl = (process.env.ACOS_API_BASE_URL || 'http://127.0.0.1:8080').replace(/\/$/, '');
const explicitUrl = process.env.ACOS_OPENAPI_URL;

const candidates = explicitUrl
  ? [explicitUrl]
  : [
      `${baseUrl}/v3/api-docs`,
      `${baseUrl}/v3/api-docs.yaml`,
      `${baseUrl}/api-docs`,
      `${baseUrl}/openapi.yaml`,
    ];

fs.mkdirSync(openapiDir, { recursive: true });

async function fetchText(url) {
  const response = await fetch(url, {
    headers: { Accept: 'application/json, application/yaml, text/yaml, */*' },
  });
  if (!response.ok) {
    throw new Error(`HTTP ${response.status} for ${url}`);
  }
  return {
    url,
    contentType: response.headers.get('content-type') || '',
    body: await response.text(),
  };
}

function looksLikeJson(text) {
  const trimmed = text.trim();
  return trimmed.startsWith('{') || trimmed.startsWith('[');
}

let lastError;
for (const url of candidates) {
  try {
    process.stdout.write(`Downloading OpenAPI from ${url} ... `);
    const result = await fetchText(url);
    if (!looksLikeJson(result.body) && !result.contentType.includes('json')) {
      // Persist raw YAML download for normalize step; also keep a JSON sibling when possible.
      const yamlOut = path.join(openapiDir, 'acos-api.yaml');
      fs.writeFileSync(yamlOut, result.body, 'utf8');
      console.log('saved YAML');
      console.log(`Wrote ${path.relative(root, yamlOut)}`);
      process.exit(0);
    }
    JSON.parse(result.body);
    fs.writeFileSync(jsonOut, result.body, 'utf8');
    console.log('ok');
    console.log(`Wrote ${path.relative(root, jsonOut)} (${result.body.length} bytes)`);
    process.exit(0);
  } catch (error) {
    lastError = error;
    console.log('failed');
    console.error(`  ${error.message}`);
  }
}

console.error('Unable to download OpenAPI specification from any candidate URL.');
console.error(lastError?.message || 'Unknown error');
process.exit(1);
