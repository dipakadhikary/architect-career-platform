#!/usr/bin/env node
/**
 * Composes a self-contained OpenAPI document from modular domain specs.
 * Preserves named domain schemas for high-quality code generation.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import SwaggerParser from '@apidevtools/swagger-parser';
import yaml from 'js-yaml';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const outDir = path.join(root, 'dist', 'openapi');
const outFile = path.join(outDir, 'ai-platform-v1.bundled.yaml');
const outJson = path.join(outDir, 'ai-platform-v1.bundled.json');

const domainSpecs = [
  'openapi/knowledge/knowledge-api.yaml',
  'openapi/learning/learning-api.yaml',
  'openapi/career/career-api.yaml',
  'openapi/portfolio/portfolio-api.yaml',
  'openapi/chat/chat-api.yaml',
];

fs.mkdirSync(outDir, { recursive: true });

const composed = {
  openapi: '3.1.0',
  info: {
    title: 'ACOS AI Platform API',
    description:
      'Aggregated synchronous REST contracts for the ACOS AI Platform (composed from modular domain specs).',
    version: '1.0.0',
    contact: {
      name: 'ACOS Platform Engineering',
      email: 'platform@acos.local',
    },
    license: {
      name: 'Proprietary',
      url: 'https://acos.local/licenses/proprietary',
    },
  },
  servers: [
    { url: 'http://localhost:8090', description: 'Local AI Platform' },
    { url: 'https://ai.acos.local', description: 'Production AI Platform' },
  ],
  tags: [
    { name: 'Health' },
    { name: 'Knowledge AI' },
    { name: 'Learning AI' },
    { name: 'Career AI' },
    { name: 'Portfolio AI' },
    { name: 'Chat AI' },
  ],
  security: [{ bearerJwt: [] }],
  paths: {},
  components: {
    securitySchemes: {},
    schemas: {},
    parameters: {},
    headers: {},
    responses: {},
  },
};

for (const relative of domainSpecs) {
  const absolute = path.join(root, relative);
  const api = await SwaggerParser.dereference(absolute, {
    dereference: { circular: 'ignore' },
  });

  Object.assign(composed.paths, api.paths || {});

  const components = api.components || {};
  mergeMap(composed.components.securitySchemes, components.securitySchemes);
  mergeMap(composed.components.schemas, components.schemas);
  mergeMap(composed.components.parameters, components.parameters);
  mergeMap(composed.components.headers, components.headers);
  mergeMap(composed.components.responses, components.responses);
}

const plain = JSON.parse(JSON.stringify(composed));
fs.writeFileSync(
  outFile,
  yaml.dump(plain, { lineWidth: 120, noRefs: true, sortingKeys: false }),
  'utf8',
);
fs.writeFileSync(outJson, `${JSON.stringify(plain, null, 2)}\n`, 'utf8');

console.log(`Bundled OpenAPI -> ${path.relative(root, outFile)}`);
console.log(`Paths: ${Object.keys(plain.paths).length}`);
console.log(`Schemas: ${Object.keys(plain.components.schemas).length}`);

function mergeMap(target, source = {}) {
  for (const [key, value] of Object.entries(source)) {
    if (!(key in target)) {
      target[key] = value;
    }
  }
}
